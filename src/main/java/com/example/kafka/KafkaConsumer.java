package com.example.kafka;

import com.example.domain.enums.FILTER;
import com.example.filter.GrayFilter;
import com.example.filter.IntegrationFilter;
import com.example.filter.MyImageFilter;
import com.example.filter.RotateFilter;
import com.example.filter.SharpFilter;
import com.example.kafka.message.ImageDone;
import com.example.kafka.message.ImageWip;
import com.example.service.ImageFiltersService;
import com.example.service.MinioService;
import java.util.List;
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
  private final MinioService minioService;
  private final String TOPIC_WIP = "images.wip";


  @KafkaListener(
      topics = TOPIC_WIP,
      groupId = "consumer-wip-gray",
      concurrency = "2",
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
  public void consumeGray(
      final ConsumerRecord<String, ImageWip> record,
      final Acknowledgment acknowledgment
  ) throws Exception {
    consume(record,
        acknowledgment,
        GrayFilter::applyFilter,
        FILTER.GRAY
    );
  }

  @KafkaListener(
      topics = TOPIC_WIP,
      groupId = "consumer-wip-sharp",
      concurrency = "2",
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
  public void consumeSharp(
      final ConsumerRecord<String, ImageWip> record,
      final Acknowledgment acknowledgment
  ) throws Exception {
    consume(record,
        acknowledgment,
        SharpFilter::applyFilter,
        FILTER.SHARP
    );
  }


  @KafkaListener(
      topics = TOPIC_WIP,
      groupId = "consumer-wip-rotate",
      concurrency = "2",
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
  public void consumeRotate(
      final ConsumerRecord<String, ImageWip> record,
      final Acknowledgment acknowledgment
  ) throws Exception {
    consume(record,
        acknowledgment,
        RotateFilter::applyFilter,
        FILTER.ROTATE
    );
  }


  @KafkaListener(
      topics = TOPIC_WIP,
      groupId = "consumer-wip-tags",
      concurrency = "2",
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
  public void consumeTags(
      final ConsumerRecord<String, ImageWip> record,
      final Acknowledgment acknowledgment
  ) throws Exception {
    consume(record,
        acknowledgment,
        IntegrationFilter::applyFilter,
        FILTER.TAGS
    );
  }

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


  public void consume(ConsumerRecord<String, ImageWip> record, Acknowledgment acknowledgment,
                      MyImageFilter imageFilter, FILTER filter) throws Exception {
    log.info("""
        Получено следующее сообщение из топика {}:
        key: {},
        value: {}
        """, record.topic(), record.key(), record.value());

    ImageWip message = record.value();
    List<String> filters = message.getFilters();
    if (filters.isEmpty() || !filters.get(0).equals(filter.name())) {
      acknowledgment.acknowledge();
      return;
    }
    message.getFilters().remove(0);
    String mediaType = "image/png";
    byte[] image = minioService.downloadImage(String.valueOf(message.getImageId()));
    byte[] resultImage = imageFilter.applyFilter(image, mediaType);

    String newImageId;
    ProducerRecord<String, Object> producerRecord;
    if (message.getFilters().isEmpty()) {
      newImageId = minioService.uploadImage(resultImage, mediaType);
      producerRecord = new ProducerRecord<>(
          "images.done",
          new ImageDone(UUID.fromString(newImageId), message.getRequestId())
      );
    } else {
      newImageId = minioService.uploadImage(
          resultImage,
          mediaType,
          "expiry/"
      );
      message.setImageId(UUID.fromString(newImageId));
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
