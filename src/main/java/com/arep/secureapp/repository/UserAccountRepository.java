package com.arep.secureapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arep.secureapp.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);
}
