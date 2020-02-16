package com.lxgolovin.sandbox.pdf.compare.report;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CompareReport {

    private final List<CompareError> compareErrors = new ArrayList<>();

    private boolean hasErrors;

    public void add(@NonNull CompareError error) {
        this.compareErrors.add(error);
        this.hasErrors = !compareErrors.isEmpty();
    }

    public void addAll(@NonNull List<CompareError> errorList) {
        this.compareErrors.addAll(errorList);
        this.hasErrors = !compareErrors.isEmpty();
    }
}
