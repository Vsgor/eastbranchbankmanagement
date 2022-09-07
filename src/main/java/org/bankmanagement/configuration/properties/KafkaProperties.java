package org.bankmanagement.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties("kafka")
public class KafkaProperties {
    @NotBlank
    private String bootstrapAddress;
    @NotEmpty
    private Map<String, String> topic;
    @Positive
    private int partitionCount;
    @Positive
    private int replicaCount;
}
