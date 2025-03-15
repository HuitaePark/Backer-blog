package com.baki.backer.domain.image;

import com.baki.backer.domain.member.Member;
import com.baki.backer.domain.member.MemberRepository;
import com.baki.backer.domain.post.Post;
import com.baki.backer.domain.post.repository.PostRepository;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OracleImageService implements ImageService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    private static final String BUCKET_NAME = "backer-image";
    private static final String BUCKET_NAME_SPACE = "axrj2zzrti3z";
    private static final String PROFILE_IMG_DIR = "profile/";
    public static final String DEFAULT_URI_PREFIX = "https://" + BUCKET_NAME_SPACE + ".objectstorage."
            + Region.AP_CHUNCHEON_1.getRegionId() + ".oci.customer-oci.com";
    private static final String POST_IMG_DIR = "post/";
    private static final long PRE_AUTH_EXPIRE_MINUTE = 20;
    private final PostRepository postRepository;

    public ObjectStorage getClient() throws Exception {
        ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse("~/.oci/config", "DEFAULT");

        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);

        return ObjectStorageClient.builder()
                .region(Region.AP_CHUNCHEON_1)
                .build(provider);
    }

    public UploadManager getManager(ObjectStorage client) throws Exception {
        UploadConfiguration configuration = UploadConfiguration.builder()
                .allowMultipartUploads(true)
                .allowParallelUploads(true)
                .build();
        return new UploadManager(client, configuration);
    }

    @Override
    public Long uploadProfileImage(MultipartFile file, Long memberId) throws Exception {
        // 임시 파일 생성: 파일명을 그대로 사용하거나 원하는 이름으로 변경 가능
        File tempFile = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());

        // MultipartFile의 내용을 임시 파일로 저장
        file.transferTo(tempFile);
        String fileDir = memberId + "/" + PROFILE_IMG_DIR;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("error : user no found"));
        return upload(tempFile, fileDir, member);
    }

    @Override
    public Long uploadPostImage(MultipartFile file, Long postId) throws Exception {
        // 임시 파일 생성: 파일명을 그대로 사용하거나 원하는 이름으로 변경 가능
        File tempFile = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());

        // MultipartFile의 내용을 임시 파일로 저장
        file.transferTo(tempFile);
        String fileDir = postId + "/" + PROFILE_IMG_DIR;
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("error : user no found"));
        return upload(tempFile, fileDir, post.getMember());
    }

    public Long upload(File uploadFile, String dirName, Member member) throws Exception {
        ObjectStorage client = getClient();
        UploadManager uploadManager = getManager(client);

        String fileName = dirName + UUID.randomUUID() + uploadFile.getName(); // 버킷에 저장된 파일 이름
        String contentType = "img/" + fileName.substring(fileName.length() - 3); // PNG, JPG 만 가능함
        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(BUCKET_NAME)
                        .namespaceName(BUCKET_NAME_SPACE)
                        .objectName(fileName)
                        .contentType(contentType)
                        .build();
        UploadRequest uploadDetails =
                UploadRequest.builder(uploadFile).allowOverwrite(true).build(request);

        UploadResponse response = uploadManager.upload(uploadDetails);
        log.info("Upload Success. File : {}", fileName);

        client.close();
        removeNewFile(uploadFile);
        return saveImageToMember(member, fileName);
    }

    public Long saveImageToMember(Member member, String fileName) {
        Image image = Image.builder()
                .member(member)
                .imgUri(fileName)
                .imageType(ImageType.PROFILE)
                .build();
        if (fileName.contains(PROFILE_IMG_DIR)) {
            image.updateImageType(ImageType.PROFILE);
        }
        imageRepository.save(image);
        member.getProfilePictures().add(image);
        return image.getId();
    }

    private void addAccessUriToMember(Member member, AuthenticatedRequest request, String imgUrl) {
        List<Image> images = member.getProfilePictures();
        for (Image img : images) {
            if (img.getImgUri().equals(imgUrl)) {
                img.updateAccessUri(DEFAULT_URI_PREFIX + request.getAccessUri());
                img.updateParId(request.authenticateId);
            }
        }
    }

    public void removeNewFile(File targetFile) {
        log.info("@@@@@@@@ 지울 대상 파일 이름" + targetFile.getName());
        log.info("@@@@@@@@ 지울 대상 파일 경로" + targetFile.getPath());
        if (targetFile.exists()) {
            if (targetFile.delete()) {
                log.info("@@@@@@@@ File delete success");
                return;
            }
            log.info("@@@@@@@@ File delete fail.");
        }
        log.info("@@@@@@@@ File not exist.");
    }


    @Override
    public String getPublicImgUrl(Long imgId, Long memberId) throws Exception {
        ObjectStorage client = getClient();
        Image img = imageRepository.findById(imgId).orElseThrow(() -> new RuntimeException("image no found"));
        AuthenticatedRequest authenticatedRequest = getPreAuth(img.getImgUri());

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("image no found"));
        addAccessUriToMember(member, authenticatedRequest, img.getAccessUri());
        return DEFAULT_URI_PREFIX + authenticatedRequest.getAccessUri();
    }

    @Override
    public MultipartFile downloadImg(Long imageId, Long memberId) throws Exception {
        return null;
    }

    @Override
    public void deleteImg(Image image) throws Exception {
        ObjectStorage client = getClient();
        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucketName(BUCKET_NAME)
                        .namespaceName(BUCKET_NAME_SPACE)
                        .objectName(image.getImgUri())
                        .build();

        deletePreAuth(image.getParId());
        imageRepository.delete(image);

        client.deleteObject(request);
        client.close();
    }

    public AuthenticatedRequest getPreAuth(String imgUrl) throws Exception {
        ObjectStorage client = getClient();

        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.DECEMBER, 30);

        Date expireTime = cal.getTime();

        log.info("권한을 얻어오기 위해 시도중입니다. 파일 이름 : {}", imgUrl);
        CreatePreauthenticatedRequestDetails details =
                CreatePreauthenticatedRequestDetails.builder()
                        .accessType(AccessType.ObjectReadWrite)
                        .objectName(imgUrl)
                        .timeExpires(expireTime)
                        .name(imgUrl)
                        .build();

        CreatePreauthenticatedRequestRequest request =
                CreatePreauthenticatedRequestRequest.builder()
                        .namespaceName(BUCKET_NAME_SPACE)
                        .bucketName(BUCKET_NAME)
                        .createPreauthenticatedRequestDetails(details)
                        .build();

        CreatePreauthenticatedRequestResponse response = client.createPreauthenticatedRequest(request);
        client.close();
        return AuthenticatedRequest.builder()
                .authenticateId(response.getPreauthenticatedRequest().getId())
                .accessUri(response.getPreauthenticatedRequest().getAccessUri())
                .build();
    }

    private void deletePreAuth(String parId) throws Exception {
        ObjectStorage client = getClient();
        DeletePreauthenticatedRequestRequest request =
                DeletePreauthenticatedRequestRequest.builder()
                        .namespaceName(BUCKET_NAME_SPACE)
                        .bucketName(BUCKET_NAME)
                        .parId(parId)
                        .build();

        client.deletePreauthenticatedRequest(request);
        client.close();
    }

    @Data
    @Builder
    static class AuthenticatedRequest {
        String accessUri;
        String authenticateId;
        String imgUrl;
    }
}
