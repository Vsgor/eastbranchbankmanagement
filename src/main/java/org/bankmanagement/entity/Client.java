package org.bankmanagement.entity;

import lombok.Getter;
import lombok.Setter;
import org.bankmanagement.enums.Role;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;

        Client client = (Client) o;

        if (active != client.active) return false;
        if (!Objects.equals(id, client.id)) return false;
        if (!email.equals(client.email)) return false;
        if (!username.equals(client.username)) return false;
        if (!password.equals(client.password)) return false;
        if (role != client.role) return false;
        return Objects.equals(slots, client.slots);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + email.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (slots != null ? slots.hashCode() : 0);
        return result;
    }
}
