package com.mapvine.stocks.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StockDetailsTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testPositiveSharesOwned() {
        expected.expect(IllegalArgumentException.class);
        new StockDetails(new Stock("", "", "250.00"), -1);
    }

}