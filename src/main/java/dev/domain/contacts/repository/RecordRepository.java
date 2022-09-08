package dev.domain.contacts.repository;

import dev.domain.contacts.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
