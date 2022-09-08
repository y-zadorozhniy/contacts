package dev.domain.contacts.repository;

import dev.domain.contacts.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findTagByName(String name);
}
