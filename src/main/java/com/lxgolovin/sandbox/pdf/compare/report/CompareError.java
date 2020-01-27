package com.lxgolovin.sandbox.pdf.compare.report;

import lombok.Getter;

import java.util.Optional;

public class CompareError {

    @Getter
    private final ErrorType errorType;

    @Getter
    private final String errorDetails;

    public CompareError(ErrorType errorType, String errorDetails) {
        this.errorType = Optional.ofNullable(errorType).orElseThrow(() -> new IllegalArgumentException("Error type cannot be null"));
        this.errorDetails = Optional.ofNullable(errorDetails).orElse(errorType.name());
    }
}
