package com.example.phase1_fams.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.phase1_fams.model.Users;

import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(u.name) LIKE CONCAT('%', LOWER(:keywords), '%')) OR (LOWER(u.email) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(u.dob >= :startDate) AND " +
            "(u.dob <= :endDate) AND " +
            "(:genders IS NULL OR u.gender IN :genders) AND" +
            "(:roles IS NULL OR u.role.roleName IN :roles)")
    Page<Users> searchByDateNotNull(@Param("keywords") String keyword, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                   @Param("genders") List<String> genders, @Param("roles") List<String> roles, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(u.name) LIKE CONCAT('%', LOWER(:keywords), '%')) OR (LOWER(u.email) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(:genders IS NULL OR u.gender IN :genders) AND" +
            "(:roles IS NULL OR u.role.roleName IN :roles)")
    Page<Users> searchByDateNull(@Param("keywords") String keyword, @Param("genders") List<String>genders, @Param("roles") List<String> roles, Pageable pageable);

    Optional<Users> findById(Long id);

    List<Users> findAllByRole_RoleIdAndStatusAndNameContainingIgnoreCase(int id, boolean status, String name);

    List<Users> findAllByRole_RoleIdAndStatus(int id, boolean status);

    Optional<Integer> countAllByStatus(boolean status);
}
