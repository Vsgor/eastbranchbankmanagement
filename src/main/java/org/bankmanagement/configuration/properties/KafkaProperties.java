package org.bankmanagement.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties("kafka")
public class KafkaProperties {
    @NotBlank
    private String bootstrapAddress;
    @NotEmpty
    private Map<String, String> topic;
}
