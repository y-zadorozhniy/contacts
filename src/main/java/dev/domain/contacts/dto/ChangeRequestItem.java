package dev.domain.contacts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.thymeleaf.util.StringUtils;

@Data
@Builder
@AllArgsConstructor
public class ChangeRequestItem {

    private String fieldName;
    private String previousValue;
    private String newValue;

    public boolean isChanged() {
        return !StringUtils.equals(previousValue, newValue);
    }
}
