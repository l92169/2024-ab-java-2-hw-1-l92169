package com.example.kafka;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;


@Configuration
@RequiredArgsConstructor
public class KafkaInitializer {

  private final KafkaProperties properties;

  @Bean
  public NewTopic imagesWip() {
    return new NewTopic("images.wip", 1, (short) 3).configs(
        Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2"));
  }

  @Bean
  public NewTopic imagesDone() {
    return new NewTopic("images.done", 1, (short) 3).configs(
        Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2"));
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    var props = properties.buildProducerProperties(null);

    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    props.put(ProducerConfig.ACKS_CONFIG, "all");

    props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class);

    props.put(ProducerConfig.LINGER_MS_CONFIG, 0);

    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.RETRIES_CONFIG, 7);
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);


    props.put(ProducerConfig.CLIENT_ID_CONFIG, "api-producer");

    return new DefaultKafkaProducerFactory<>(props);
  }

}
