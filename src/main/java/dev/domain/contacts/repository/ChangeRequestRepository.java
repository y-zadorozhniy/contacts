package dev.domain.contacts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.domain.contacts.domain.ChangeRequest;

import java.util.List;

public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findByUserId(Long userId);

    List<ChangeRequest> findByStatus(String status);
}
