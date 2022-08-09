package org.bankmanagement.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "slot", schema = "accounts")
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state", nullable = false)
    private long state;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slot)) return false;

        Slot slot = (Slot) o;

        if (state != slot.state) return false;
        if (active != slot.active) return false;
        if (!Objects.equals(id, slot.id)) return false;
        return client.equals(slot.client);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (state ^ (state >>> 32));
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + client.hashCode();
        return result;
    }
}
