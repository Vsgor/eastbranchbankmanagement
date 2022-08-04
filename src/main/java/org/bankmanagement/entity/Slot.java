package org.bankmanagement.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "slot", schema = "accounts")
public class Slot {

    @Id
    private long id;

    @Column(name = "state", nullable = false)
    private long state;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "active", nullable = false)
    private boolean active;
}
