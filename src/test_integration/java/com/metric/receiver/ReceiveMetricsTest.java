package com.metric.receiver;

import com.metric.receiver.dto.ResponseDTO;
import com.metric.receiver.dto.TemperatureDTO;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReceiveMetricsTest {
    @ClassRule
    public static KafkaEmbedded embeddedKafka =
            new KafkaEmbedded(1, true, "temperature");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private KafkaConfiguration configuration;

    private CountDownLatch latch;

    private KafkaMessageListenerContainer<String, TemperatureDTO> container;

    private List<TemperatureDTO> measurements = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        DefaultKafkaConsumerFactory<String, TemperatureDTO> consumerFactory =
                configuration.consumerFactory(embeddedKafka);
        ContainerProperties containerProperties = new ContainerProperties("temperature");
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setupMessageListener((MessageListener<String, TemperatureDTO>) record -> {
            latch.countDown();
            measurements.add(record.value());
        });
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    @After
    public void tearDown() {
        container.stop();
    }

    /**
     * Send temperature to rest api, check it in kafka
     * @throws InterruptedException
     */
    @Test
    public void temperatureCheck() throws InterruptedException {
        List<TemperatureDTO> metrics = new ArrayList<>();
        metrics.add(new TemperatureDTO("uuid1", 19, new Date()));
        metrics.add(new TemperatureDTO("uuid1", 20.3, new Date()));
        metrics.add(new TemperatureDTO("uuid2", 21, new Date()));
        latch = new CountDownLatch(metrics.size());
        for (TemperatureDTO metric : metrics) {
            ResponseDTO responseDTO =
                    this.restTemplate.postForObject(
                            "http://localhost:" + port + "/api/v1/sensors/" + metric.getSensorUuid() + "/measurements",
                            metric,
                            ResponseDTO.class);
            Assert.assertTrue(responseDTO.isResult());
            Assert.assertNotNull(responseDTO.getResponse());
        }
        latch.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(0, latch.getCount());
        Assert.assertEquals(3, metrics.size());
        Assert.assertEquals(metrics, measurements);
    }

}
