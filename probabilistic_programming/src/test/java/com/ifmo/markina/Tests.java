package com.ifmo.markina;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Tests {

    @Test
    public void flipTest() {
        Context context = new Context();
        context.defineFlip("d1", new BigDecimal("0.3"));

        Map<Integer, BigDecimal> actual = context.infer("d1");
        Map<Integer, BigDecimal> expected = new HashMap<>();
        expected.put(0, new BigDecimal("0.3"));
        expected.put(1, new BigDecimal("0.7"));

        assertEqualsMap("Invalid \"d1\" probability", expected, actual);
    }

    @Test
    public void multinomialTest() {
        Context context = new Context();
        Map<Integer, BigDecimal> mapD2 = new HashMap<>();
        mapD2.put(2, new BigDecimal("0.1"));
        mapD2.put(3, new BigDecimal("0.3"));
        mapD2.put(4, new BigDecimal("0.6"));
        context.defineMultinomial("d2", mapD2);

        Map<Integer, BigDecimal> actual = context.infer("d2");
        Map<Integer, BigDecimal> expected = new HashMap<>();
        expected.put(2, new BigDecimal("0.1"));
        expected.put(3, new BigDecimal("0.3"));
        expected.put(4, new BigDecimal("0.6"));

        assertEqualsMap("Invalid \"d2\" probability", expected, actual);
    }

    @Test
    public void threeDefinitionsTest() {
        Context context = new Context();
        context.defineFlip("d1", new BigDecimal("0.3"));

        Map<Integer, BigDecimal> mapD2 = new HashMap<>();
        mapD2.put(2, new BigDecimal("0.1"));
        mapD2.put(3, new BigDecimal("0.3"));
        mapD2.put(4, new BigDecimal("0.6"));
        context.defineMultinomial("d2", mapD2);

        Map<Integer, BigDecimal> mapD3 = new HashMap<>();
        mapD3.put(5, new BigDecimal("0.9"));
        mapD3.put(6, new BigDecimal("0.05"));
        mapD3.put(7, new BigDecimal("0.05"));
        context.defineMultinomial("d3", mapD3);

        Map<Integer, BigDecimal> actualD1 = context.infer("d1");
        Map<Integer, BigDecimal> expectedD1 = new HashMap<>();
        expectedD1.put(0, new BigDecimal("0.3"));
        expectedD1.put(1, new BigDecimal("0.7"));

        assertEqualsMap("Invalid \"d1\" probability", expectedD1, actualD1);

        Map<Integer, BigDecimal> actualD2 = context.infer("d2");
        Map<Integer, BigDecimal> expectedD2 = new HashMap<>();
        expectedD2.put(2, new BigDecimal("0.1"));
        expectedD2.put(3, new BigDecimal("0.3"));
        expectedD2.put(4, new BigDecimal("0.6"));

        assertEqualsMap("Invalid \"d2\" probability", expectedD2, actualD2);

        Map<Integer, BigDecimal> actualD3 = context.infer("d3");
        Map<Integer, BigDecimal> expectedD3 = new HashMap<>();
        expectedD3.put(5, new BigDecimal("0.9"));
        expectedD3.put(6, new BigDecimal("0.05"));
        expectedD3.put(7, new BigDecimal("0.05"));

        assertEqualsMap("Invalid \"d3\" probability", expectedD3, actualD3);
    }

    @Test
    public void observeTest() {
        Context context = new Context();
        context.defineFlip("d1", new BigDecimal("0.3"));

        Map<Integer, BigDecimal> mapD2 = new HashMap<>();
        mapD2.put(2, new BigDecimal("0.1"));
        mapD2.put(3, new BigDecimal("0.3"));
        mapD2.put(4, new BigDecimal("0.6"));
        context.defineMultinomial("d2", mapD2);

        // d1 + d2 >= 4
        context.observe(map -> (map.get("d1") + map.get("d2")) >= 4);

        Map<Integer, BigDecimal> actualD1 = context.infer("d1");
        Map<Integer, BigDecimal> expectedD1 = new HashMap<>();
        expectedD1.put(0, new BigDecimal("0.22"));
        expectedD1.put(1, new BigDecimal("0.77"));


        Map<Integer, BigDecimal> actualD2 = context.infer("d2");
        Map<Integer, BigDecimal> expectedD2 = new HashMap<>();
        expectedD2.put(2, new BigDecimal("0"));
        expectedD2.put(3, new BigDecimal("0.26"));
        expectedD2.put(4, new BigDecimal("0.74"));


        System.out.println(actualD1);
        System.out.println(actualD2);
        assertAboutEqualsMap("Invalid \"d1\" probability", expectedD1, actualD1);
        assertAboutEqualsMap("Invalid \"d2\" probability", expectedD2, actualD2);
    }

    private void assertAboutEqualsMap(String msg,
                                      Map<Integer, BigDecimal> expected,
                                      Map<Integer, BigDecimal> actual) {
        assertEquals(msg + ": invalid size" + msg, expected.size(), actual.size());

        for (Integer key : expected.keySet()) {
            assertTrue(msg + ": " + key + " doesn't contains in result", actual.containsKey(key));

            assertTrue(msg, expected.get(key).setScale(2, BigDecimal.ROUND_HALF_UP)
                    .compareTo(actual.get(key).setScale(2, BigDecimal.ROUND_HALF_UP)) == 0);
        }

    }

    private void assertEqualsMap(String msg, Map<Integer, BigDecimal> expected, Map<Integer, BigDecimal> actual) {
        assertEquals(msg + ": invalid size" + msg, expected.size(), actual.size());

        for (Integer key : expected.keySet()) {
            assertTrue(msg + ": " + key + " doesn't contains in result", actual.containsKey(key));

            assertTrue(msg, expected.get(key).compareTo(actual.get(key)) == 0);
        }
    }

}
