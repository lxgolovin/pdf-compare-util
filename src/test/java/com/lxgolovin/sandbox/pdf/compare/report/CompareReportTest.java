package com.lxgolovin.sandbox.pdf.compare.report;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CompareReportTest {

    @ParameterizedTest
    @NullSource
    void checkCompareErrorIsNull(CompareError compareError) {
        CompareReport report = new CompareReport();
        assertThrows(NullPointerException.class, () -> report.add(compareError));
    }

    @ParameterizedTest
    @NullSource
    void checkArgumentIsNull(List<CompareError> list) {
        CompareReport report = new CompareReport();
        assertThrows(NullPointerException.class, () -> report.addAll(list));
    }
}