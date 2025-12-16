package com.hospital.repository;

import com.hospital.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 你已有的（别删）
    Optional<User> findByLoginNameAndStatus(String loginName, Integer status);
    Optional<User> findByLoginName(String loginName);

    // ✅ AdminUserServiceImpl 用到的：关键字分页
    Page<User> findByLoginNameContaining(String keyword, Pageable pageable);

    // ✅ 关键字 + role + status 分页（你报错的就是这个）
    Page<User> findByLoginNameContainingAndRoleAndStatus(String keyword, String role, Integer status, Pageable pageable);

    // ✅（建议补上）只按 role
    Page<User> findByLoginNameContainingAndRole(String keyword, String role, Pageable pageable);

    // ✅（建议补上）只按 status
    Page<User> findByLoginNameContainingAndStatus(String keyword, Integer status, Pageable pageable);

    // ✅（给 create 用）检查登录名是否重复
    boolean existsByLoginName(String loginName);
}
