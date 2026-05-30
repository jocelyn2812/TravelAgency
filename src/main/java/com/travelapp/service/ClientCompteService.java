package com.travelapp.service;

import com.travelapp.model.ClientCompte;
import com.travelapp.repository
    .ClientCompteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority
    .SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password
    .PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientCompteService
        implements UserDetailsService {

    private final ClientCompteRepository repository;
    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(
            String email)
            throws UsernameNotFoundException {

        System.out.println(
            "🔍 Tentative connexion client : "
            + email);

        ClientCompte client = repository
            .findByEmail(email)
            .orElseThrow(() -> {
                System.out.println(
                    "❌ Client non trouvé : "
                    + email);
                return new UsernameNotFoundException(
                    "Client non trouvé : " + email);
            });

        System.out.println(
            "✅ Client trouvé : "
            + client.getEmail()
            + " actif=" + client.isActif());

        return new User(
            client.getEmail(),
            client.getPassword(),
            client.isActif(),
            true, true, true,
            List.of(new SimpleGrantedAuthority(
                "ROLE_CLIENT")));
    }

    public ClientCompte inscrire(
            ClientCompte client,
            String confirmPassword) {

        if (repository.existsByEmail(
                client.getEmail())) {
            throw new RuntimeException(
                "Cet email est déjà utilisé !");
        }

        if (!client.getPassword()
                .equals(confirmPassword)) {
            throw new RuntimeException(
                "Les mots de passe ne "
                + "correspondent pas !");
        }

        client.setPassword(
            passwordEncoder.encode(
                client.getPassword()));

        ClientCompte saved =
            repository.save(client);

        System.out.println(
            "✅ Nouveau client inscrit : "
            + saved.getEmail());

        return saved;
    }

    public Optional<ClientCompte> findByEmail(
            String email) {
        return repository.findByEmail(email);
    }

    public Optional<ClientCompte> findById(
            Long id) {
        return repository.findById(id);
    }

    public ClientCompte save(
            ClientCompte client) {
        return repository.save(client);
    }

    public long countAll() {
        return repository.count();
    }
}