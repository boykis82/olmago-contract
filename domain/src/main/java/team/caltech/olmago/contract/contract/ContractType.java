package team.caltech.olmago.contract.contract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import team.caltech.olmago.contract.exception.InvalidArgumentException;

import java.util.Arrays;

public enum ContractType {
  PACKAGE("PACKAGE"),
  OPTION("OPTION"),
  UNIT("UNIT");
  
  private final String value;
  
  ContractType(String value) {
    this.value = value;
  }
  
  @JsonCreator
  public static ContractType from(String value) {
    return Arrays.stream(ContractType.values())
        .filter(contractType -> contractType.value.equals(value))
        .findFirst()
        .orElseThrow(InvalidArgumentException::new);
  }
  
  @JsonValue
  public String getValue() {
    return value;
  }
}
