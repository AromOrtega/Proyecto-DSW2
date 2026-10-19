package com.saludvital.controller;

import com.saludvital.Paciente;
import com.saludvital.Role;
import com.saludvital.User;
import com.saludvital.dto.JwtResponse;
import com.saludvital.dto.LoginRequest;
import com.saludvital.dto.MessageResponse;
import com.saludvital.dto.SignupRequest;
import com.saludvital.dto.SignupPacienteRequest;
import com.saludvital.enums.ERole;
import com.saludvital.repository.PacienteRepository;
import com.saludvital.repository.RoleRepository;
import com.saludvital.repository.UserRepository;
import com.saludvital.security.jwt.JwtUtils;
import com.saludvital.security.services.UserDetailsImpl;
import com.saludvital.security.services.UserDetailsServiceImpl;
import com.saludvital.service.PacienteRegistroService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private PacienteRepository pacienteRepository;

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @Autowired
        private PasswordEncoder encoder;

        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private PacienteRegistroService pacienteRegistroService;

        // ===================== LOGIN =====================
        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                loginRequest.getUsername(), loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities()
                                .stream()
                                .map(item -> item.getAuthority())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(
                                new JwtResponse(
                                                jwt,
                                                userDetails.getId(),
                                                userDetails.getUsername(),
                                                userDetails.getEmail(),
                                                roles));
        }

        // ===================== REGISTER (SOLO PACIENTE) =====================
        @PostMapping("/register")
        @Transactional
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

                if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                        return ResponseEntity.badRequest()
                                        .body(new MessageResponse("Error: El nombre de usuario ya está en uso."));
                }

                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                        return ResponseEntity.badRequest()
                                        .body(new MessageResponse("Error: El correo electrónico ya está registrado."));
                }

                // 1) Crear Usuario (Credenciales)
                User user = new User(
                                signUpRequest.getUsername(),
                                signUpRequest.getEmail(),
                                encoder.encode(signUpRequest.getPassword()));

                // 2) Forzar rol PACIENTE (ignorar lo que venga en role)
                Set<Role> roles = new HashSet<>();
                Role pacienteRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                                .orElseThrow(() -> new RuntimeException("Error: Role PACIENTE no encontrado."));
                roles.add(pacienteRole);

                user.setRoles(roles);
                User savedUser = userRepository.save(user);

                // 3) Crear Perfil PACIENTE
                Paciente paciente = new Paciente();
                paciente.setUsuarioId(savedUser.getId());

                paciente.setNombre(signUpRequest.getNombre() != null ? signUpRequest.getNombre() : "");
                paciente.setApellido(signUpRequest.getApellido() != null ? signUpRequest.getApellido() : "");
                paciente.setNumeroIdentificacion(signUpRequest.getNumeroIdentificacion());

                if (signUpRequest.getFechaNacimiento() != null && !signUpRequest.getFechaNacimiento().isBlank()) {
                        paciente.setFechaNacimiento(LocalDate.parse(signUpRequest.getFechaNacimiento()));
                }

                paciente.setTelefono(signUpRequest.getTelefono());
                paciente.setDireccion(signUpRequest.getDireccion());

                pacienteRepository.save(paciente);

                return ResponseEntity.ok(new MessageResponse("¡Usuario registrado exitosamente!"));
        }

        // ===================== REGISTRO PÚBLICO DE PACIENTE =====================
        /**
         * Endpoint público: cualquier visitante puede autoregistrarse como paciente.
         * No requiere token.
         *
         * POST /api/auth/register-paciente
         */
        @PostMapping("/register-paciente")
        public ResponseEntity<?> registerPacientePublico(
                        @Valid @RequestBody SignupPacienteRequest request) {
                pacienteRegistroService.registrar(request);
                return ResponseEntity.ok(
                                new MessageResponse("¡Paciente registrado exitosamente! Ya puedes iniciar sesión."));
        }

        // ===================== REGISTRO DE PACIENTE POR ADMIN =====================
        /**
         * Endpoint privado: solo accesible con un token de ROLE_ADMIN.
         * Usa el mismo body JSON que el registro público.
         *
         * POST /api/auth/admin/register-paciente
         */
        @PostMapping("/admin/register-paciente")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> registerPacienteAdmin(
                        @Valid @RequestBody SignupPacienteRequest request) {
                pacienteRegistroService.registrar(request);
                return ResponseEntity.ok(
                                new MessageResponse("Paciente registrado por administrador exitosamente."));
        }

        // ===================== REFRESH TOKEN =====================
        @PostMapping("/refresh")
        public ResponseEntity<?> refreshToken(HttpServletRequest request) {
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String jwt = authHeader.substring(7);

                        if (jwtUtils.validateJwtToken(jwt)) {
                                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities());

                                String newJwt = jwtUtils.generateJwtToken(authentication);

                                return ResponseEntity.ok(
                                                new JwtResponse(
                                                                newJwt,
                                                                ((UserDetailsImpl) userDetails).getId(),
                                                                userDetails.getUsername(),
                                                                ((UserDetailsImpl) userDetails).getEmail(),
                                                                userDetails.getAuthorities()
                                                                                .stream()
                                                                                .map(item -> item.getAuthority())
                                                                                .collect(Collectors.toList())));
                        }
                }
                return ResponseEntity.badRequest()
                                .body(new MessageResponse("Error: Token inválido, expirado o no proporcionado"));
        }
}