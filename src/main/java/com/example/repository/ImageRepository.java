package com.example.repository;

import com.example.domain.ImageT;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageT, UUID> {
  boolean existsImagesByIdIn(List<UUID> ids);

  boolean existsImageById(UUID link);

  Optional<ImageT> findImageById(UUID id);

  List<ImageT> findAllByIdIn(List<UUID> ids);

  List<ImageT> findAllByUserId(Long id);
}
