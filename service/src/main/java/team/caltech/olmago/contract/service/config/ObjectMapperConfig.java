package team.caltech.olmago.contract.service.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class ObjectMapperConfig {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
  
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    
    JavaTimeModule javaTimeModule = new JavaTimeModule();
//    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
//    javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
//    javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
//    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
    mapper.registerModule(javaTimeModule);
    
    return mapper;
  }
  
//  private static class LocalDateSerializer extends JsonSerializer<LocalDate> {
//    @Override
//    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//      gen.writeString(value.format(DATE_FORMATTER));
//    }
//  }
//
//  private static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
//    @Override
//    public LocalDate deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
//      return LocalDate.parse(p.getValueAsString(), DATE_FORMATTER);
//    }
//  }
//
//  private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
//    @Override
//    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//      gen.writeString(value.format(DATETIME_FORMATTER));
//    }
//  }
//
//  private static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
//    @Override
//    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
//      return LocalDateTime.parse(p.getValueAsString(), DATETIME_FORMATTER);
//    }
//  }
}
