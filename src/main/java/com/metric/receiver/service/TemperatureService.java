package com.metric.receiver.service;

import com.metric.receiver.config.KafkaProducerConfig;
import com.metric.receiver.dto.ResponseDTO;
import com.metric.receiver.dto.TemperatureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TemperatureService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureService.class);

    @Autowired
    private KafkaTemplate<String, TemperatureDTO> template;

    @Autowired
    private KafkaProducerConfig config;

    @Async
    public CompletableFuture<ResponseDTO<?>> report(TemperatureDTO metric) {
        LOGGER.debug("send {} to {}", metric, config.getKafkaTopic());
        template.send(config.getKafkaTopic(), metric);
        return CompletableFuture.completedFuture(new ResponseDTO<>("OK"));
    }
}
