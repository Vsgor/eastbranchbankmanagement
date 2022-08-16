package org.bankmanagement.mapper;

import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Slot;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SlotMapperTest {

    private final SlotMapper mapper = Mappers.getMapper(SlotMapper.class);

    @Test
    void mapToDto() {
        Client client = new Client();
        client.setId(13L);
        client.setActive(false);

        Slot slot = new Slot();
        slot.setId(592L);
        slot.setActive(false);
        slot.setState(928390111);
        slot.setClient(client);

        client.setSlots(List.of(slot));

        SlotDto slotDto = mapper.mapToDto(slot);

        assertAll(
                () -> assertThat(slotDto.getId()).isEqualTo(slot.getId()),
                () -> assertThat(slotDto.getState()).isEqualTo(slot.getState()),
                () -> assertThat(slotDto.isActive()).isEqualTo(slot.isActive())
        );
    }
}