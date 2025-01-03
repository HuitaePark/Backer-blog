package com.baki.backer.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    boolean existsByUsername(String username);
    boolean existsByName(String name);
    boolean existsById(@NonNull Long id);
    Optional<Member> findByUsername(String username);

    @Query("select m.id from Member m where m.username = :username")
    Long findIdByUsername(@Param("username") String username);


}
