package dev.domain.contacts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsExclude;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id", nullable = false, unique = true)
    @EqualsExclude
    private Long id;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "comment")
    private String comment;

    @Column(name = "type")
    private String type;
}