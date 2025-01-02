package com.baki.backer.domain.member.repository;

import com.baki.backer.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Integer> {
    boolean existsByUsername(String username);
    boolean existsByName(String name);
    boolean existsById(@NonNull Integer id);
    Optional<Member> findByUsername(String username);

    @Query("select m.id from Member m where m.username = :username")
    Integer findIdByUsername(@Param("username") String username);


}
