package com.bjfu.paperSystem.PasswordResetCode.Dao;

import com.bjfu.paperSystem.javabeans.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PasswordResetCodeDao extends JpaRepository<PasswordResetCode, Long> {
    // 通过userid找最新验证码
    Optional<PasswordResetCode> findByUserId(Integer userId);

    @Modifying
    @Transactional
    int deleteByUserId(Integer userId);
}
