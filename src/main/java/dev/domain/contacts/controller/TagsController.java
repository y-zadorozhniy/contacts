package dev.domain.contacts.controller;

import dev.domain.contacts.dto.response.TagResponse;
import dev.domain.contacts.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class TagsController {

    private final TagService tagService;

    @GetMapping("/api/tags")
    public List<TagResponse> getTags() {
        return tagService.getTags();
    }
}
