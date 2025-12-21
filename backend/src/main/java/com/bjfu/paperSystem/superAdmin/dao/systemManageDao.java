package com.bjfu.paperSystem.superAdmin.dao;

import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface systemManageDao extends JpaRepository<Logs, Integer>, JpaSpecificationExecutor<Logs> {
}
