package com.bjfu.paperSystem.author.dao;

import com.bjfu.paperSystem.javabeans.Versions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionsDao extends JpaRepository<Versions, Integer> {
}