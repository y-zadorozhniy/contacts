package dev.domain.contacts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChangeRequestStatusResponse {

    private Long recordId;
    private String changeType;
    private String status;
    private String comment;

}
