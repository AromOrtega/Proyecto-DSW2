package com.saludvital.service;

import com.saludvital.HorarioAtencion;
import com.saludvital.Medico;
import com.saludvital.Role;
import com.saludvital.User;
import com.saludvital.dto.MedicoDTO;
import com.saludvital.enums.ERole;
import com.saludvital.enums.Especialidad;
import com.saludvital.messaging.MedicoEventPublisher;
import com.saludvital.repository.MedicoRepository;
import com.saludvital.repository.RoleRepository;
import com.saludvital.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MedicoEventPublisher publisher;

    public MedicoService(MedicoRepository medicoRepository,
                         UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         MedicoEventPublisher publisher) {
        this.medicoRepository = medicoRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.publisher = publisher;
    }

    // --- CRUD Básico ---
    public List<Medico> findAll() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> findById(Long id) {
        return medicoRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<Medico> medicoOpt = medicoRepository.findById(id);
        
        if (medicoOpt.isPresent()) {
            Medico medico = medicoOpt.get();
            Long userId = medico.getUserId();

            // 1. Borramos primero el perfil del médico
            medicoRepository.deleteById(id);

            // 2. Borramos la cuenta de usuario asociada
            if (userId != null) {
                userRepository.deleteById(userId);
            }

            // 3. Notificamos el evento
            publisher.publicarEvento("ELIMINADO", id);
        }
    }

    public boolean existsById(Long id) {
        return medicoRepository.existsById(id);
    }

    public long contarMedicos() {
        return medicoRepository.count();
    }

    // --- Validaciones ---
    public boolean existsByNumeroLicencia(String numeroLicencia) {
        if (numeroLicencia == null)
            return false;
        return medicoRepository.existsByNumeroLicencia(numeroLicencia.trim());
    }

    public boolean existsByNumeroLicenciaAndIdNot(String numeroLicencia, Long id) {
        if (numeroLicencia == null)
            return false;
        return medicoRepository.existsByNumeroLicenciaAndIdNot(numeroLicencia.trim(), id);
    }

    public List<Medico> findByEspecialidad(String especialidadStr) {
        if (especialidadStr == null)
            return Collections.emptyList();
        try {
            Especialidad esp = Especialidad.valueOf(especialidadStr.trim().toUpperCase(Locale.ROOT));
            return medicoRepository.findByEspecialidad(esp);
        } catch (IllegalArgumentException ex) {
            return Collections.emptyList();
        }
    }

    public Optional<Medico> findByUserId(Long userId) {
        return medicoRepository.findByUserId(userId);
    }

    // ============================================================
    // REGISTRO DE MÉDICO (solo ADMIN) — Una única transacción
    // Crea: 1 User (con ROLE_MEDICO, password BCrypt) + 1 Medico
    // ============================================================
    @Transactional
    public Medico registrarMedico(MedicoDTO dto) {

        // --- Validación de datos del usuario ---
        if (dto.usuario() == null
                || dto.usuario().username() == null || dto.usuario().username().isBlank()
                || dto.usuario().password() == null || dto.usuario().password().isBlank()) {
            throw new IllegalArgumentException("El username y la contraseña del usuario son obligatorios");
        }
        if (dto.email() == null || dto.email().isBlank()) {
            throw new IllegalArgumentException("El email del médico es obligatorio");
        }

        // --- Validar unicidad ---
        if (userRepository.existsByUsername(dto.usuario().username().trim())) {
            throw new IllegalArgumentException(
                    "El nombre de usuario '" + dto.usuario().username() + "' ya está en uso");
        }
        if (userRepository.existsByEmail(dto.email().trim())) {
            throw new IllegalArgumentException("El email '" + dto.email() + "' ya está registrado");
        }
        String lic = dto.numeroLicencia() != null ? dto.numeroLicencia().trim() : null;
        if (lic == null || lic.isBlank()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio");
        }
        if (medicoRepository.existsByNumeroLicencia(lic)) {
            throw new IllegalArgumentException("El número de licencia '" + lic + "' ya está registrado");
        }

        // --- 1) Crear User con ROLE_MEDICO y password encriptado ---
        User user = new User(
                dto.usuario().username().trim(),
                dto.email().trim(),
                passwordEncoder.encode(dto.usuario().password()));

        Role rolMedico = roleRepository.findByName(ERole.ROLE_MEDICO)
                .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_MEDICO no encontrado en la BD"));

        Set<Role> roles = new HashSet<>();
        roles.add(rolMedico);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // --- 2) Crear Medico con referencia al User recién guardado ---
        Medico medico = new Medico();
        medico.setNombre(dto.nombre());
        medico.setApellido(dto.apellido());
        medico.setNumeroLicencia(lic);
        medico.setTelefono(dto.telefono());
        medico.setEmail(dto.email().trim());
        medico.setEspecialidad(dto.especialidad());
        medico.setEstado(dto.estado());
        medico.setDisponible(dto.disponible());
        medico.setTarifaConsulta(dto.tarifaConsulta());
        medico.setUserId(savedUser.getId());

        // --- ✨ PROCESAMIENTO DE HORARIOS (CREATE) ✨ ---
        if (dto.horarios() != null && !dto.horarios().isEmpty()) {
            dto.horarios().forEach(hDto -> {
                HorarioAtencion horario = new HorarioAtencion();
                horario.setDia(hDto.dia());
                horario.setHoraInicio(hDto.horaInicio());
                horario.setHoraFin(hDto.horaFin());
                
                medico.addHorario(horario); 
            });
        }

        Medico saved = medicoRepository.save(medico);
        publisher.publicarEvento("CREADO", saved.getId());

        return saved;
    }

    // ============================================================
    // --- Guardar médico existente (UPDATE usando DTO) ---
    // ============================================================
    @Transactional
    public Medico actualizarMedico(Long id, MedicoDTO dto) {
        // 1. Buscamos el médico en la BD
        Medico existente = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        String lic = dto.numeroLicencia() != null ? dto.numeroLicencia().trim() : null;
        if (lic == null || lic.isBlank()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio");
        }
        if (existsByNumeroLicenciaAndIdNot(lic, id)) {
            throw new IllegalArgumentException("El número de licencia ya pertenece a otro médico");
        }

        // 2. Actualizamos datos básicos
        existente.setNombre(dto.nombre());
        existente.setApellido(dto.apellido());
        existente.setNumeroLicencia(lic);
        existente.setEspecialidad(dto.especialidad());
        existente.setEmail(dto.email());
        existente.setTelefono(dto.telefono());
        existente.setEstado(dto.estado());
        existente.setDisponible(dto.disponible());
        existente.setTarifaConsulta(dto.tarifaConsulta());

        // 3. Limpiamos los horarios viejos
        existente.getHorarios().clear();

        // 4. ✨ Reconstruimos con los horarios que vienen intactos en el DTO ✨
        if (dto.horarios() != null && !dto.horarios().isEmpty()) {
            dto.horarios().forEach(hDto -> {
                HorarioAtencion nuevoHorario = new HorarioAtencion();
                nuevoHorario.setDia(hDto.dia());
                nuevoHorario.setHoraInicio(hDto.horaInicio());
                nuevoHorario.setHoraFin(hDto.horaFin());
                
                existente.addHorario(nuevoHorario); 
            });
        }

        Medico saved = medicoRepository.save(existente);
        publisher.publicarEvento("ACTUALIZADO", saved.getId());
        return saved;
    }
}