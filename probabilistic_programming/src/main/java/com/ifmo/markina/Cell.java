package com.ifmo.markina;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Cell {
    private BigDecimal p;

    private final Map<String, Integer> variable;

    public Cell(BigDecimal p, Map<String, Integer> variable) {
        this.p = p;
        this.variable = new HashMap<>(variable);
    }

    public Cell(BigDecimal p) {
        this.p = p;
        this.variable = new HashMap<>();
    }

    public BigDecimal getP() {
        return p;
    }

    public void setP(BigDecimal p) {
        this.p = p;
    }

    public Map<String, Integer> getVariable() {
        return variable;
    }

    public void addVariable(String name, Integer value) {
        // TODO check if exist
        variable.put(name, value);
    }
}
