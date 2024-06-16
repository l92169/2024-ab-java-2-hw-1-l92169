package com.example.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "images")
@Data
@Accessors(chain = true)
public class ImageT {
  @Id
  private UUID id;
  @Column(length = 100)
  private String name;
  private Long size;
  @Column(length = 300)
  private String link;
  private Long userId;
}
