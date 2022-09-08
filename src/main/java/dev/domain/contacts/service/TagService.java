package dev.domain.contacts.service;

import dev.domain.contacts.dto.response.TagResponse;

import java.util.List;

public interface TagService {

    List<TagResponse> getTags();
}
