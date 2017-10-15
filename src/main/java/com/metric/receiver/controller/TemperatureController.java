package com.metric.receiver.controller;

import com.metric.receiver.dto.ResponseDTO;
import com.metric.receiver.dto.TemperatureDTO;
import com.metric.receiver.service.TemperatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@RestController
public class TemperatureController {
    private static final MediaType CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private TemperatureService responseService;

    @RequestMapping(path = "/api/v1/sensors/{uuid}/measurements", method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity<?>> respond(
            @PathVariable("uuid") String uuid, @Valid @RequestBody TemperatureDTO temperature) {
        temperature.setSensorUuid(uuid);
        CompletableFuture<ResponseDTO<?>> respond = responseService.report(temperature);
        return respond.thenApply(this::returnResult);
    }

    private ResponseEntity<ResponseDTO<?>> returnResult(ResponseDTO<?> result) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(CONTENT_TYPE);
        if (result.isResult())
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        return new ResponseEntity<>(result, headers, HttpStatus.BAD_REQUEST);
    }
}
