package com.travelapp.config;

import com.travelapp.model.Destination;
import com.travelapp.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DestinationConverter 
        implements Converter<String, Destination> {

    private final DestinationRepository destinationRepository;

    @Override
    public Destination convert(String id) {
        if (id == null || id.isEmpty() || id.equals("")) {
            return null;
        }
        try {
            return destinationRepository
                .findById(Long.parseLong(id))
                .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}