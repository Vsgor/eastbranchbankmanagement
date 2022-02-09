package org.bankmanagement.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@Table(name = "slot", schema = "accounts")
public class Slot {

    @Id
    @SequenceGenerator(name = "slotSeq", sequenceName = "slot_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slotSeq")
    @Column(name = "id")
    private long id;

    @Column(name = "state", nullable = false)
    private long state;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "active", nullable = false)
    private boolean active;
}
