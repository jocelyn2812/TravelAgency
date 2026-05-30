package com.travelapp.repository;

import com.travelapp.model.ClientCompte;
import org.springframework.data.jpa.repository
    .JpaRepository;
import java.util.Optional;

public interface ClientCompteRepository
        extends JpaRepository<ClientCompte, Long> {

    Optional<ClientCompte> findByEmail(String email);
    boolean existsByEmail(String email);
}