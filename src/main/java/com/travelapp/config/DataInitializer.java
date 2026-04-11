package com.travelapp.config;

import com.travelapp.model.Role;
import com.travelapp.model.Role.NomRole;
import com.travelapp.model.Utilisateur;
import com.travelapp.repository.RoleRepository;
import com.travelapp.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Créer les rôles
        for (NomRole nomRole : NomRole.values()) {
            if (roleRepository.findByNom(nomRole).isEmpty()) {
                Role role = new Role();
                role.setNom(nomRole);
                roleRepository.save(role);
            }
        }

        // Créer admin par défaut
        if (!utilisateurRepository.existsByUsername("admin")) {
            Role roleAdmin = roleRepository
                .findByNom(NomRole.ROLE_ADMIN).get();
            Utilisateur admin = new Utilisateur();
            admin.setUsername("admin");
            admin.setPassword(
                passwordEncoder.encode("admin123"));
            admin.setEmail("admin@travel.com");
            admin.setNom("Administrateur");
            admin.setPrenom("Super");
            admin.setRoles(Set.of(roleAdmin));
            utilisateurRepository.save(admin);
            System.out.println(
                "✅ Admin créé : admin / admin123");
        }
    }
}