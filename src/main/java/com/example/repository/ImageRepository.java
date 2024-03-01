package com.example.repository;

import com.example.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    boolean existsImagesByIdIn(List<Integer> ids);
    boolean existsImagesByLink(String link);
    Optional<Image> findImageById(int id);
    List<Image> findAllByIdIn(List<Integer> ids);
}
