package com.travelapp.repository;

import com.travelapp.model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DestinationRepository
        extends JpaRepository<Destination, Long> {
    List<Destination> findByActifTrue();
}