package com.travelapp.config;

import com.travelapp.model.ClientCompte;
import com.travelapp.model.Role;
import com.travelapp.model.Role.NomRole;
import com.travelapp.model.Utilisateur;
import com.travelapp.repository
    .ClientCompteRepository;
import com.travelapp.repository.RoleRepository;
import com.travelapp.repository
    .UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password
    .PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer
        implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UtilisateurRepository
        utilisateurRepository;
    private final ClientCompteRepository
        clientCompteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ===== RÔLES =====
        for (NomRole nomRole : NomRole.values()) {
            if (roleRepository.findByNom(nomRole)
                    .isEmpty()) {
                Role role = new Role();
                role.setNom(nomRole);
                roleRepository.save(role);
            }
        }

        // ===== ADMIN =====
        if (!utilisateurRepository
                .existsByUsername("admin")) {
            Role roleAdmin = roleRepository
                .findByNom(NomRole.ROLE_ADMIN)
                .get();
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
                "✅ Admin : admin / admin123");
        }

        // ===== CLIENT TEST =====
        if (!clientCompteRepository
                .existsByEmail("client@test.com")) {
            ClientCompte client =
                new ClientCompte();
            client.setNom("Rakoto");
            client.setPrenom("Jean");
            client.setEmail("client@test.com");
            client.setPassword(
                passwordEncoder.encode(
                    "Client123"));
            client.setTelephone("+261340000000");
            client.setActif(true);
            clientCompteRepository.save(client);
            System.out.println(
                "✅ Client test : "
                + "client@test.com / Client123");
        }
    }
}