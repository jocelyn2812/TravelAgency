package com.travelapp.config;

import com.travelapp.model.Voyage;
import com.travelapp.repository.VoyageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoyageConverter 
        implements Converter<String, Voyage> {

    private final VoyageRepository voyageRepository;

    @Override
    public Voyage convert(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        try {
            return voyageRepository
                .findById(Long.parseLong(id))
                .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}