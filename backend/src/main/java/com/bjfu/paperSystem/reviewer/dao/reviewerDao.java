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
public interface reviewerDao extends JpaRepository<Review, Integer> {
    @Query("""
    select r from Review r
    join fetch r.manuscript m
    where (:start is null or r.invitationTime >= :start)
      and (:end is null or r.invitationTime <= :end)
    order by r.invitationTime asc
""")
    List<Review> findWithManuscript(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);
}