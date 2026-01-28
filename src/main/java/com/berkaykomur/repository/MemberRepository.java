package com.berkaykomur.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.berkaykomur.model.Member;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	 
	 @Query("SELECT m FROM Member m JOIN m.user u WHERE u.username = :username")
	    Optional<Member> findByUsername(@Param("username") String username);
	 
	     // Yeni eklenen metot:
	 @Query("SELECT m.id FROM Member m JOIN m.user u WHERE u.username = :username")
	     Optional<Long> findMemberIdByUsername(@Param("username") String username);
 }

