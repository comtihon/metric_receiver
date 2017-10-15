package com.metric.receiver;

import com.metric.receiver.dto.ResponseDTO;
import com.metric.receiver.dto.TemperatureDTO;
import com.metric.receiver.service.TemperatureService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TemperatureService service;

    /**
     * No temperature - should fail
     */
    @Test
    public void loadIncorrect() {
        TemperatureDTO temp = new TemperatureDTO();

        ResponseDTO responseDTO =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/v1/sensors/uuid1/measurements",
                        temp,
                        ResponseDTO.class);
        Assert.assertFalse(responseDTO.isResult());

        temp.setSensorUuid("uuid1");
        responseDTO =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/v1/sensors/uuid1/measurements",
                        temp,
                        ResponseDTO.class);
        Assert.assertFalse(responseDTO.isResult());
    }

    /**
     * No uuid in post body - should pass
     */
    @Test
    public void loadNoUuid() {
        given(service.report(any(TemperatureDTO.class))).willReturn(mockReturn("ok"));

        TemperatureDTO temp = new TemperatureDTO();
        temp.setTemperature(20);
        ResponseDTO responseDTO =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/v1/sensors/uuid1/measurements",
                        temp,
                        ResponseDTO.class);
        Assert.assertTrue(responseDTO.isResult());
    }

    /**
     * Should ignore uuid, provided in body
     */
    @Test
    public void differentUuid() {
        given(service.report(any(TemperatureDTO.class))).willReturn(mockReturn("ok"));

        TemperatureDTO temp = new TemperatureDTO("uuid2", 20.9);
        ResponseDTO responseDTO =
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/api/v1/sensors/uuid1/measurements",
                        temp,
                        ResponseDTO.class);
        Assert.assertTrue(responseDTO.isResult());
        temp.setSensorUuid("uuid1");
        verify(service).report(temp);
    }

    private CompletableFuture<ResponseDTO<?>> mockReturn(String data) {
        return CompletableFuture.completedFuture(new ResponseDTO<>(data));
    }
}
