package dev.domain.contacts.mappers;

import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<S, T> {

    T map(S source);

    default List<T> mapAll(List<S> source) {
        return source.stream()
                .map(this::map)
                .collect(Collectors.toList());
    }
}
