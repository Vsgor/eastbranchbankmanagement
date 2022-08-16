package org.bankmanagement.repository;

import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Slot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SlotRepositoryTest {

    @Autowired
    private SlotRepository slotRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    void saveSlot_WithNoClient_ShouldThrowException() {
        Slot slot = getSlot(null, null);

        assertThatThrownBy(() -> slotRepository.saveAndFlush(slot))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void saveSlot_WithClient() {
        Optional<Client> client = clientRepository.findById(1L);
        assertTrue(client.isPresent());

        Slot slot = getSlot(null, client.get());

        slotRepository.saveAndFlush(slot);

        assertThat(slot.getId()).isNotNull();
        assertThat(slot).isEqualTo(getSlot(slot.getId(), client.get()));
    }

    private Slot getSlot(Long id, Client client) {
        Slot slot = new Slot();
        slot.setId(id);
        slot.setClient(client);
        slot.setActive(false);
        return slot;
    }
}