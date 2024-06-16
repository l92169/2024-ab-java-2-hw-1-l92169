package com.example.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Table(name = "images")
@Accessors(chain = true)
public class ImageT {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @Column(length = 100)
  private String name;
  private Long size;
  @Column(length = 300)
  private String link;
  private Long userId;
}
