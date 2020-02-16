package com.lxgolovin.sandbox.pdf.compare.report;

import lombok.NonNull;
import lombok.Value;

@Value
public class CompareError {

    @NonNull
    private final ErrorType errorType;

    @NonNull
    private final String errorDetails;
}
