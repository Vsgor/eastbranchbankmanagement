package org.bankmanagement.mapper;

import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.entity.Slot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SlotMapper {

    SlotDto mapToDto(Slot slot);

}
