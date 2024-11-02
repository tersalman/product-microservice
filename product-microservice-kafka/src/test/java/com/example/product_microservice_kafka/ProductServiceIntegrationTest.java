package com.example.product_microservice_kafka;

import com.example.core.ProductCreatedEvent;
import com.example.product_microservice_kafka.service.ProductService;
import com.example.product_microservice_kafka.service.dto.CreatedProductDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.*;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class ProductServiceIntegrationTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private Environment environment;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<String, ProductCreatedEvent> container;

    private BlockingQueue<ConsumerRecord<String, ProductCreatedEvent>> records;


    @BeforeAll
    void setUp() {
        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties(environment.getProperty("product-created-events-topic-name"));

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, ProductCreatedEvent>) records::add);
        try {
            container.start();
        } catch (Exception e) {
            System.out.println(" we got error in this step" );
        }
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());


    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(
                BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id", "product-created-events"),
                JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages", "com.example.core"),
                AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset", "earliest")
        );
    }

    @Test
    void testCreateProduct_whenGivenValidProductDetails_successfullySendKafkaMessage() throws ExecutionException, InterruptedException {
        //Arrange
        String title = "Samsung";
        BigDecimal price = new BigDecimal(600);
        Integer quantity = 1;

        CreatedProductDto createdProductDto = new CreatedProductDto(title, price, quantity);

        //Act
        productService.createProductDto(createdProductDto);

        //Assert
        ConsumerRecord<String, ProductCreatedEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
         Assertions.assertNotNull(message);
        Assertions.assertNotNull(message.key());
        ProductCreatedEvent productCreatedEvent = message.value();
        Assertions.assertEquals(createdProductDto.getQuantity(), productCreatedEvent.getQuantity());
        Assertions.assertEquals(createdProductDto.getTitle(), productCreatedEvent.getTitle());
        Assertions.assertEquals(createdProductDto.getPrice(), productCreatedEvent.getPrice());
    }

    @DirtiesContext
    @AfterAll
    void tearDown() {
        container.stop();
    }

}
