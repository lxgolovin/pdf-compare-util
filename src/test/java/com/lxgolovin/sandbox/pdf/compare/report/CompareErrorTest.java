package com.lxgolovin.sandbox.pdf.compare.report;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

class CompareErrorTest {

    @ParameterizedTest
    @NullSource
    void checkArgumentIsNull(String errorDetails) {
        assertThrows(NullPointerException.class, () -> new CompareError(ErrorType.LINE_INSERTED, errorDetails));
    }

    @ParameterizedTest
    @NullSource
    void checkTypeArgumentIsNull(ErrorType errorType) {
        assertThrows(NullPointerException.class, () -> new CompareError(errorType, ErrorType.LINE_INSERTED.name()));
    }
}