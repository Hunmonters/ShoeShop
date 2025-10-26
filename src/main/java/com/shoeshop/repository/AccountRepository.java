package com.shoeshop.repository;

import com.shoeshop.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    // Tìm chính xác theo email (nhưng vẫn trim để tránh lỗi khoảng trắng)
    @Query("SELECT a FROM Account a WHERE TRIM(a.email) = TRIM(:email)")
    Optional<Account> findByEmail(String email);

    // Tìm email không phân biệt hoa/thường + trim
    @Query("SELECT a FROM Account a WHERE LOWER(TRIM(a.email)) = LOWER(TRIM(:email))")
    Optional<Account> findByEmailIgnoreCase(String email);

    // Tìm theo username
    Optional<Account> findByUsername(String username);

    // Check tồn tại theo email
    boolean existsByEmail(String email);

    // Check tồn tại theo username
    boolean existsByUsername(String username);
}
