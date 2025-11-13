package com.inventra.app.repository;

import com.inventra.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Búsqueda paginada por coincidencia parcial en nombre o username (case-insensitive)
    Page<User> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String name, String username, Pageable pageable);

    // Usuarios con rol distinto a ADMIN (role_id != :roleId) con filtro opcional por nombre/username
    @Query("SELECT u FROM User u WHERE u.role.id <> :roleId AND (:name IS NULL OR :name = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<User> searchNonAdmin(@Param("name") String name, @Param("roleId") Long roleId, Pageable pageable);
}
