package dev.domain.contacts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChangeRequestInfo {

    private Long id;
    private String recordId;
    private String contactName;
    private String changeType;
    private String userName;
    private String createdAt;
    private String status;
    private String comment;
}
