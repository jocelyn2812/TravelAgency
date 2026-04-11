package com.travelapp.config;

import com.travelapp.model.Client;
import com.travelapp.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientConverter 
        implements Converter<String, Client> {

    private final ClientRepository clientRepository;

    @Override
    public Client convert(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        try {
            return clientRepository
                .findById(Long.parseLong(id))
                .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}