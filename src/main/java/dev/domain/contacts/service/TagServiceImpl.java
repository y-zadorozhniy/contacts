package dev.domain.contacts.service;

import dev.domain.contacts.dto.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dev.domain.contacts.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> getTags() {
        return tagRepository.findAll()
                .stream()
                .map(TagResponse::toDto)
                .collect(Collectors.toList());
    }
}
