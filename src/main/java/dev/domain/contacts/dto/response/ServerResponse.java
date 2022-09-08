package dev.domain.contacts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ServerResponse {

    private String message;
    private String status;
}
