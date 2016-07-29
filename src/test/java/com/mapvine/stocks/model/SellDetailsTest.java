package com.mapvine.stocks.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.math.BigDecimal;

public class SellDetailsTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testPositiveSharesSell() {
        expected.expect(IllegalArgumentException.class);
        new SellDetails(new BigDecimal(2.00), -1);
    }

}