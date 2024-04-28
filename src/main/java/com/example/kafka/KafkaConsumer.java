package com.example.kafka;

import com.example.kafka.message.ImageDone;
import com.example.kafka.message.ImageWip;
import com.example.service.ImageFiltersService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final ImageFiltersService imageFiltersService;

  @KafkaListener(
      topics = "images.done",
      groupId = "done",
      properties = {
          ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG + "=false",
          ConsumerConfig.ISOLATION_LEVEL_CONFIG + "=read_committed",
          ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG +
              "=org.apache.kafka.clients.consumer.RoundRobinAssignor",
          ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG +
              "=org.apache.kafka.common.serialization.StringDeserializer",
          ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG +
              "=org.springframework.kafka.support.serializer.JsonDeserializer",
          JsonDeserializer.TRUSTED_PACKAGES + "=com.example.kafka.message",
          ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest"
      }
  )
  public void consumeDone(ConsumerRecord<String, ImageDone> record, Acknowledgment acknowledgment) {
    log.info("""
        Получено следующее сообщение из топика {}:
        key: {},
        value: {}
        """, record.topic(), record.key(), record.value());
    try {
      imageFiltersService.setDone(record.value().getImageId(), record.value().getRequestId());
      acknowledgment.acknowledge();
      log.info("Успешно обработано сообщение {}", record.value().getImageId());
    } catch (Exception e) {
      log.error("Не удалось обработать сообщение Кафки ", e);
    }
  }

  @KafkaListener(
      topics = "images.wip",
      groupId = "wip",
      properties = {
          ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG + "=false",
          ConsumerConfig.ISOLATION_LEVEL_CONFIG + "=read_committed",
          ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG +
              "=org.apache.kafka.clients.consumer.RoundRobinAssignor",
          ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG +
              "=org.apache.kafka.common.serialization.StringDeserializer",
          ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG +
              "=org.springframework.kafka.support.serializer.JsonDeserializer",
          JsonDeserializer.TRUSTED_PACKAGES + "=com.example.kafka.message",
          ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest"
      }
  )
  public void consumeWip(ConsumerRecord<String, ImageWip> record, Acknowledgment acknowledgment) {
    log.info("""
        Получено следующее сообщение из топика {}:
        key: {},
        value: {}
        """, record.topic(), record.key(), record.value());
    ImageWip message = record.value();
    if (message.getFilters().isEmpty()) {
      log.error("Empty WIP filters");
      message.getFilters().add("NULL");
    }
    message.getFilters().remove(0);
    UUID newImageId = UUID.randomUUID();

    ProducerRecord<String, Object> producerRecord;

    if (message.getFilters().isEmpty()) {
      producerRecord = new ProducerRecord<>(
          "images.done",
          new ImageDone(newImageId, message.getRequestId())
      );
    } else {
      producerRecord = new ProducerRecord<>(
          "images.wip",
          message
      );
    }
    try {
      kafkaTemplate.send(producerRecord).join();
      acknowledgment.acknowledge();
      log.info("Успешно обработано сообщение {}", record.value().getImageId());
    } catch (Exception e) {
      log.error("Не удалось обработать сообщение Кафки ", e);
    }
  }
}
