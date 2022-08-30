package org.bankmanagement.kafka;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.aspect.log.LogMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@RequiredArgsConstructor
public class DownloadSchedulerProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.downloader}")
    private String topic;

    @LogMethod
    public void sendDownloadRequest(URL downloadUrl) {
        kafkaTemplate.send(topic, downloadUrl.toString());
    }
}
