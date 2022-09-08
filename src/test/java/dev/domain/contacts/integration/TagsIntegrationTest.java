package dev.domain.contacts.integration;

import dev.domain.contacts.utils.TestIOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import dev.domain.contacts.domain.Tag;
import dev.domain.contacts.repository.TagRepository;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TagsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @WithAnonymousUser
    void tagsListTest_denyForAnonymousUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/tags"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(username = "editor", authorities = {"EDITOR"})
    void tagsListTest() throws Exception {
        //arrange
        tagRepository.saveAll(List.of(Tag.builder()
                        .name("Tag 1")
                        .build(),
                Tag.builder()
                        .name("Tag 2")
                        .build()));
        String expected = TestIOUtils.getResourceAsString("/web/response/tags/TagsListResponse.json");
        //act & assert
        mvc.perform(MockMvcRequestBuilders.get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected));
    }
}
