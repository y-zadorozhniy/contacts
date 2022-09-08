package dev.domain.contacts.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@UtilityClass
public class TestIOUtils {

    @SneakyThrows
    public String getResourceAsString(String s) {
        return IOUtils.toString(Objects.requireNonNull(TestIOUtils.class.getResource(s)), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public String getResourceAsStringWithPlaceholders(String s, Object... placeholders) {
        String content = getResourceAsString(s);
        for (Object placeholder : placeholders) {
            content = StringUtils.replaceOnce(content, "PLACEHOLDER", placeholder.toString());
        }
        return content;
    }
}
