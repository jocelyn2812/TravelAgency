package com.travelapp.service;

import com.travelapp.model.Destination;
import com.travelapp.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;

    public List<Destination> findAll() {
        return destinationRepository.findAll();
    }

    public Optional<Destination> findById(Long id) {
        return destinationRepository.findById(id);
    }

    public Destination save(Destination destination) {
        return destinationRepository.save(destination);
    }

    public void delete(Long id) {
        destinationRepository.deleteById(id);
    }

    public long countAll() {
        return destinationRepository.count();
    }
}