package com.saludvital.security;

import com.saludvital.Role;
import com.saludvital.User;
import com.saludvital.enums.ERole;
import com.saludvital.repository.RoleRepository;
import com.saludvital.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Crea automáticamente los roles y el usuario ADMIN al iniciar la app
 * si aún no existen en la base de datos.
 *
 * Credenciales por defecto:
 * usuario: admin
 * password: Admin1234!
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Crear roles si no existen
        crearRolSiNoExiste(ERole.ROLE_ADMIN);
        crearRolSiNoExiste(ERole.ROLE_MEDICO);
        crearRolSiNoExiste(ERole.ROLE_PACIENTE);

        // 2. Crear usuario admin si no existe
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Role ADMIN no encontrado"));

            User admin = new User(
                    "admin",
                    "admin@saludvital.com",
                    passwordEncoder.encode("Admin1234!") // ← contraseña por defecto
            );
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            log.info("✅ Usuario ADMIN creado: username=admin | password=Admin1234!");
        } else {
            log.info("ℹ️  Usuario ADMIN ya existe, no se crea de nuevo.");
        }
    }

    private void crearRolSiNoExiste(ERole nombre) {
        if (roleRepository.findByName(nombre).isEmpty()) {
            roleRepository.save(new Role(nombre));
            log.info("✅ Rol creado: {}", nombre.name());
        }
    }
}
