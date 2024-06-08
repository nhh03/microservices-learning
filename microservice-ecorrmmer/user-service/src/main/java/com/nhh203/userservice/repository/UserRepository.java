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
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
        // JPQL
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String name);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(@Param("id") Long id);


    @Query("select CASE WHEN COUNT(u) > 0 then  true  else  false end from User u where u.email = :email")
    Boolean existsByEmail(String email);

    @Query("select CASE WHEN COUNT(u) > 0 then  true  else  false end from User u where u.phone = :phoneNumber")
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("select CASE WHEN COUNT(u) > 0 then  true  else  false end from User u where u.username = :username")
    Boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.phone = :phoneNumber, u.address = :address WHERE u.id = :userId")
    void updateUserContactInfo(@Param("userId") Long userId, @Param("phoneNumber") String phoneNumber, @Param("address") String address);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId")
    @Transactional
    void updatePasswordById(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}
