package com.saludvital.service;

import com.saludvital.Paciente;
import com.saludvital.Role;
import com.saludvital.User;
import com.saludvital.dto.SignupPacienteRequest;
import com.saludvital.enums.ERole;
import com.saludvital.repository.PacienteRepository;
import com.saludvital.repository.RoleRepository;
import com.saludvital.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Servicio encargado de registrar un nuevo Paciente.
 * Orquesta la creación del User (con ROLE_PACIENTE) y su perfil Paciente,
 * todo dentro de una sola transacción para garantizar consistencia.
 */
@Service
public class PacienteRegistroService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder encoder;

    public PacienteRegistroService(UserRepository userRepository,
            RoleRepository roleRepository,
            PacienteRepository pacienteRepository,
            PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.pacienteRepository = pacienteRepository;
        this.encoder = encoder;
    }

    /**
     * Registra un nuevo usuario con rol PACIENTE y crea su perfil de paciente.
     *
     * @param req Datos del formulario de registro.
     * @throws ResponseStatusException 400 si el username o email ya existen.
     */
    @Transactional
    public void registrar(SignupPacienteRequest req) {

        // ── 1. Validaciones de unicidad ────────────────────────────────────
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Error: El nombre de usuario ya está en uso.");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Error: El correo electrónico ya está registrado.");
        }

        // ── 2. Crear User con ROLE_PACIENTE ────────────────────────────────
        User user = new User(
                req.getUsername(),
                req.getEmail(),
                encoder.encode(req.getPassword()));

        Role pacienteRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error: El rol PACIENTE no está configurado en la base de datos."));

        Set<Role> roles = new HashSet<>();
        roles.add(pacienteRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // ── 3. Crear perfil Paciente ───────────────────────────────────────
        Paciente paciente = new Paciente();
        paciente.setUsuarioId(savedUser.getId());
        paciente.setNombre(req.getNombre() != null ? req.getNombre() : "");
        paciente.setApellido(req.getApellido() != null ? req.getApellido() : "");
        paciente.setNumeroIdentificacion(req.getNumeroIdentificacion());

        if (req.getFechaNacimiento() != null && !req.getFechaNacimiento().isBlank()) {
            paciente.setFechaNacimiento(LocalDate.parse(req.getFechaNacimiento()));
        }

        paciente.setTelefono(req.getTelefono());
        paciente.setDireccion(req.getDireccion());

        pacienteRepository.save(paciente);
    }
}
