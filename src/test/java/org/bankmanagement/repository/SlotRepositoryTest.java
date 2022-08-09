package org.bankmanagement.repository;

import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Slot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SlotRepositoryTest {

    @Autowired
    private SlotRepository slotRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testBeanInjected() {
        assertThat(slotRepository).isNotNull();
    }

    @Test
    void saveSlot_WithRealClient() {
        Optional<Client> client = clientRepository.findById(1L);
        assertThat(client.isPresent()).isTrue();

        Slot slot = getSlot(client.get());

        slotRepository.save(slot);

        assertThat(slot.getId()).isNotNull();
    }

    private Slot getSlot(Client client) {
        Slot slot = new Slot();
        slot.setClient(client);
        slot.setActive(false);
        return slot;
    }
}