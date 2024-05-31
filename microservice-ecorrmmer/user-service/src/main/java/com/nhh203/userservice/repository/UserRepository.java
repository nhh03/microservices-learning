package com.nhh203.userservice.repository;

import com.nhh203.userservice.model.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User , Long > {

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Query("UPDATE User u SET u.phoneNumber = :phoneNumber, u.address = :address WHERE u.id = :userId")
    void updateUserContactInfo(@Param("userId") Long userId, @Param("phoneNumber") String phoneNumber, @Param("address") String address);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId")
    @Transactional
    void updatePasswordById(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}
