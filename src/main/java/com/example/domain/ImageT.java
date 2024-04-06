package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

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
