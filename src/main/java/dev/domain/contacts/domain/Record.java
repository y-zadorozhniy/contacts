package dev.domain.contacts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "records")
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "full_name", nullable = false, unique = true)
    private String fullName;

    @Column(name = "organization")
    private String organization;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "comment")
    private String comment;

    @OneToMany(targetEntity = Contact.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id")
    private List<Contact> contacts = new ArrayList<>();

    @ManyToMany(targetEntity = Tag.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "record_tags", joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    public static boolean isContactsEquals(List<Contact> source, List<Contact> newContacts) {
        if (ObjectUtils.anyNull(source, newContacts)
                && !ObjectUtils.allNull(source, newContacts)
                || source.size() != newContacts.size()) {
            return false;
        }
        for (int i = 0; i < source.size(); i++) {
            Contact c1 = source.get(i);
            Contact c2 = newContacts.get(i);
            if (!StringUtils.equals(c1.getType(), c2.getType())
                    || !StringUtils.equals(c1.getValue(), c2.getValue())
                    || !StringUtils.equals(c1.getComment(), c2.getComment())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isTagsEquals(List<Tag> source, List<Tag> newTags) {
        if (ObjectUtils.anyNull(source, newTags)
                && !ObjectUtils.allNull(source, newTags)
                || source.size() != newTags.size()) {
            return false;
        }
        for (int i = 0; i < source.size(); i++) {
            if (!StringUtils.equals(source.get(i).getName(), newTags.get(i).getName())) {
                return false;
            }
        }
        return true;
    }
}