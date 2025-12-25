package com.bjfu.paperSystem.reviewer.dao;

import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface reviewerDao extends JpaRepository<User, Integer> {
    List<User> findByStatus(String status);

    @Query("""
        select r from Review r
        where (r.invitationTime is null or r.invitationTime >= :startTime) and (r.invitationTime is null or r.invitationTime <= :endTime)
        order by r.invitationTime asc
""")
    List<Review> findAllByInvitationTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}