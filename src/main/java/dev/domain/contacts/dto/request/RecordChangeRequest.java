package dev.domain.contacts.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RecordChangeRequest {

    private String fullName;
    private String company;
    private String occupation;
    private String country;
    private String city;
    private List<ContactRequest> contacts;
    private List<String> tags;
    private String comment;
}
