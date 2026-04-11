package com.travelapp.service;

import com.travelapp.model.Utilisateur;
import com.travelapp.repository.UtilisateurRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository,
            @Lazy PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Utilisateur user = utilisateurRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Utilisateur non trouvé : " + username));

        List<SimpleGrantedAuthority> authorities = user
            .getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(
                r.getNom().name()))
            .collect(Collectors.toList());

        return new org.springframework.security.core
            .userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActif(),
                true, true, true,
                authorities);
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur save(Utilisateur u) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return utilisateurRepository.save(u);
    }
}