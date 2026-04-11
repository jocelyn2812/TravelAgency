package com.travelapp.repository;

import com.travelapp.model.Voyage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VoyageRepository
        extends JpaRepository<Voyage, Long> {
    List<Voyage> findByActifTrue();
}