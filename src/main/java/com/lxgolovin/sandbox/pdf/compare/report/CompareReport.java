package com.lxgolovin.sandbox.pdf.compare.report;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class CompareReport {

    private final List<CompareError> compareErrors = new ArrayList<>();

    private boolean hasErrors;

    public void add(CompareError error) {
        Optional.ofNullable(error).map(compareErrors::add);
        this.hasErrors = !compareErrors.isEmpty();
    }

    public void addAll(List<CompareError> errorList) {
        Optional.ofNullable(errorList).map(compareErrors::addAll);
        this.hasErrors = !compareErrors.isEmpty();
    }
}
