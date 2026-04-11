package com.travelapp.service;

import com.travelapp.model.Hebergement;
import com.travelapp.repository.HebergementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HebergementService {

    private final HebergementRepository hebergementRepository;

    public List<Hebergement> findAll() {
        return hebergementRepository.findAll();
    }

    public Optional<Hebergement> findById(Long id) {
        return hebergementRepository.findById(id);
    }

    public Hebergement save(Hebergement h) {
        return hebergementRepository.save(h);
    }

    public void delete(Long id) {
        hebergementRepository.deleteById(id);
    }
}