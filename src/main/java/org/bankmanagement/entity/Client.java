package org.bankmanagement.entity;

import lombok.Getter;
import lombok.Setter;
import org.bankmanagement.enums.Role;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "client", schema = "accounts")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "client", orphanRemoval = true)
    private List<Slot> slots;
}
