package com.travelapp.repository;

import com.travelapp.model.Role;
import com.travelapp.model.Role.NomRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository
        extends JpaRepository<Role, Long> {
    Optional<Role> findByNom(NomRole nom);
}