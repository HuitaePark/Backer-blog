package com.baki.backer.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Integer> {
    boolean existsByUsername(String username);
    boolean existsByName(String name);
    Optional<Member> findByUsername(String username);
}
