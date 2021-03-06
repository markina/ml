package com.ifmo.markina;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;

public class Context {
    private final Map<String, Set<Integer>> variables = new HashMap<>();
    private List<Cell> cells = new ArrayList<>();

    void defineFlip(String name, BigDecimal p) {
        Map<Integer, BigDecimal> map = new HashMap<>();
        map.put(0, p);
        map.put(1, BigDecimal.ONE.subtract(p));
        defineMultinomial(name, map);
    }

    void defineMultinomial(String name, Map<Integer, BigDecimal> ps) {

        assertUniqueName(name);

        variables.put(name, new HashSet<>(ps.keySet()));

        if (cells.isEmpty()) {
            for (Map.Entry<Integer, BigDecimal> p : ps.entrySet()) {
                Cell newCell = new Cell(p.getValue());
                newCell.addVariable(name, p.getKey());
                cells.add(newCell);
            }
        } else {
            final List<Cell> newCells = new ArrayList<>();

            Map.Entry<Integer, BigDecimal> lastP = ps.entrySet().stream()
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Variable should contain at least two"));

            ps.remove(lastP.getKey());
            for (Map.Entry<Integer, BigDecimal> p : ps.entrySet()) {
                for (Cell cell : cells) {
                    Cell newCell = new Cell(cell.getP().multiply(p.getValue()), cell.getVariable());
                    newCell.addVariable(name, p.getKey());
                    newCells.add(newCell);
                }
            }

            for (Cell cell : cells) {
                Cell newCell = new Cell(cell.getP().multiply(lastP.getValue()), cell.getVariable());
                newCell.addVariable(name, lastP.getKey());
                newCells.add(newCell);
            }

            cells.clear();
            cells = newCells;
        }
    }

    public Map<Integer, BigDecimal> infer(String name) {
        assertContains(name);

        Set<Integer> values = variables.get(name);
        Map<Integer, BigDecimal> result = new HashMap<>();

        for (Integer value : values) {
            BigDecimal sum = cells.stream()
                    .filter(cell -> cell.getVariable().get(name).equals(value))
                    .map(Cell::getP)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put(value, sum);
        }
        return result;
    }

    public void observe(Function<Map<String, Integer>, Boolean> function) {
        BigDecimal sum = BigDecimal.ZERO;
        for(Cell cell: cells) {
            if(!function.apply(cell.getVariable())) {
                cell.setP(BigDecimal.ZERO);
            } else {
                sum = sum.add(cell.getP());
            }
        }

        BigDecimal mlt = BigDecimal.ONE.divide(sum, 2, RoundingMode.HALF_UP);
        for(Cell cell: cells) {
            cell.setP(cell.getP().multiply(mlt));
        }
    }

    private void assertUniqueName(String name) {
        if(variables.containsKey(name)) {
            throw new IllegalArgumentException("Name " + name + " is exist");
        }
    }

    private void assertContains(String name) {
        if(!variables.containsKey(name)) {
            throw new IllegalArgumentException("Name " + name + " is not exist");
        }
    }
}
