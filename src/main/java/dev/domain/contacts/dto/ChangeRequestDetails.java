package dev.domain.contacts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import dev.domain.contacts.dto.internal.ChangeRequestType;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ChangeRequestDetails {

    private Long id;
    private ChangeRequestType type;
    private List<ChangeRequestItem> changes;

    public boolean isEdit() {
        return ChangeRequestType.CHANGE == type;
    }
}
