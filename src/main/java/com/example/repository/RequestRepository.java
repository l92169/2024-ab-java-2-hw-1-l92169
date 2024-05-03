package com.example.repository;

import com.example.domain.Request;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
  Optional<Request> findRequestById(UUID requestId);
}
