package com.api.videostreaming.repositories;

import com.api.videostreaming.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUserName(String userName);
}