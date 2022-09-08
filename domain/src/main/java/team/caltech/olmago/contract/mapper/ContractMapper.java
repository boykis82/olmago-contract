package team.caltech.olmago.contract.mapper;

import org.mapstruct.Mapper;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.dto.ContractDto;

@Mapper(componentModel = "spring")
public interface ContractMapper {
  
  ContractDto toDto(Contract entity);
}
