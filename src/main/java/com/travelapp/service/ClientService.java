package com.travelapp.service;

import com.travelapp.model.Client;
import com.travelapp.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        clientRepository.deleteById(id);
    }

    public List<Client> rechercher(String nom) {
        return clientRepository
            .findByNomContainingIgnoreCase(nom);
    }

    public long countAll() {
        return clientRepository.count();
    }
}