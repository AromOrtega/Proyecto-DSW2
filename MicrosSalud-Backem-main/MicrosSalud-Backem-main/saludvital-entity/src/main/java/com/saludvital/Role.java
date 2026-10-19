package com.saludvital;

import com.saludvital.enums.ERole;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // 👈 CAMBIADO A LONG

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    public Role() {}

    public Role(ERole name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {   // 👈 CAMBIADO A LONG
        return id;
    }

    public void setId(Long id) {   // 👈 CAMBIADO A LONG
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}