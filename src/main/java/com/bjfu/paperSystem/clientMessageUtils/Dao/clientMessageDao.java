package com.bjfu.paperSystem.clientMessageUtils.Dao;

import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.javabeans.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface clientMessageDao extends JpaRepository<ClientMessage, Integer> {
    @Query("""
        select m from ClientMessage m
        join fetch m.sender
        join fetch m.receiver
        where m.senderId = :uid
        order by m.sendingTime desc
    """)
    List<ClientMessage> findSentWithUsers(@Param("uid") Integer uid);

    @Query("""
        select m from ClientMessage m
        join fetch m.sender
        join fetch m.receiver
        where m.receiverId = :uid
        order by m.sendingTime desc
    """)
    List<ClientMessage> findReceivedWithUsers(@Param("uid") Integer uid);

    @Query("""
    select m from ClientMessage m
    join fetch m.sender
    join fetch m.receiver
    where m.manuId = :manuId
    order by m.sendingTime asc
    """)
    List<ClientMessage> findByManuIdWithUsers(@Param("manuId") Integer manuId);
}
