package org.bankmanagement.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bankmanagement.aspect.log.LogMethod;
import org.bankmanagement.dataobject.TransactionReportDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionReportProducer {

    private final KafkaTemplate<String, TransactionReportDto> kafkaTemplate;

    @Value("${kafka.topic.report}")
    private String topic;

    @LogMethod
    public void sendTransactionInfo(TransactionReportDto reportDto) {
        ListenableFuture<SendResult<String, TransactionReportDto>> listenableFuture =
                kafkaTemplate.send(topic, reportDto);
        listenableFuture.addCallback(result -> log.info(String.valueOf(result)), ex -> log.warn(String.valueOf(ex)));

    }
}
