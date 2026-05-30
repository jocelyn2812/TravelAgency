package com.travelapp.repository;

import com.travelapp.model.Client;
import org.springframework.data.jpa.repository
    .JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClientRepository
        extends JpaRepository<Client, Long> {

    List<Client> findByNomContainingIgnoreCase(
        String nom);
    boolean existsByEmail(String email);

    // ✅ Ajouter cette méthode
    Optional<Client> findByEmail(String email);
}