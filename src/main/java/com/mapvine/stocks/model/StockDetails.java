package com.mapvine.stocks.model;

import java.math.BigDecimal;

/**
 * Created by David on 7/29/16.
 */
public class StockDetails {

    private Stock stock;
    private int sharesOwned;
    private SellDetails sellDetails;
    private BigDecimal profitLoss;

    public StockDetails(Stock stock, int sharesOwned) {
        if (sharesOwned <= 0) {
            throw new IllegalArgumentException("Shares must be greater than 0");
        }
        this.stock = stock;
        this.sharesOwned = sharesOwned;
    }

    public SellDetails getSellDetails() {
        return sellDetails;
    }

    public void setSellDetails(SellDetails sellDetails) {
        this.sellDetails = sellDetails;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getSharesOwned() {
        return sharesOwned;
    }

    public void setSharesOwned(int sharesOwned) {
        this.sharesOwned = sharesOwned;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }
}
