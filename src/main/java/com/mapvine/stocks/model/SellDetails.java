package com.mapvine.stocks.model;

import java.math.BigDecimal;

/**
 * Created by David on 7/29/16.
 */
public class SellDetails {

    private BigDecimal sellPrice;
    private int sharesToSell;

    public SellDetails(BigDecimal sellPrice, int sharesToSell) {
        if (sharesToSell <= 0) {
            throw new IllegalArgumentException("Shares must be greater than 0");
        }
        this.sellPrice = sellPrice;
        this.sharesToSell = sharesToSell;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getSharesToSell() {
        return sharesToSell;
    }

    public void setSharesToSell(int sharesToSell) {
        this.sharesToSell = sharesToSell;
    }
}
