package com.example.domain;

import com.example.domain.enums.STATUS;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Table(name = "request")
@Accessors(chain = true, fluent = true)
public class Request {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private STATUS status;
  private UUID originImageId;
  private UUID changedImageId;
}
