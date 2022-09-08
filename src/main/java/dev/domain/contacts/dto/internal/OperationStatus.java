package dev.domain.contacts.dto.internal;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

public enum OperationStatus {

    PENDING("Ожидает"),
    APPROVED("Принят"),
    DECLINED("Отменен");

    @Getter
    private final String value;

    OperationStatus(String value) {
        this.value = value;
    }

    public static OperationStatus from(String type) {
        return Arrays.stream(values())
                .filter(requestOperationStatus -> Objects.equals(requestOperationStatus.value, type) ||
                        Objects.equals(requestOperationStatus.name(), type))
                .findFirst()
                .orElseThrow();
    }
}