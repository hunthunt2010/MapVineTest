package com.mapvine.stocks;

import com.mapvine.stocks.model.SellDetails;
import com.mapvine.stocks.model.Stock;
import com.mapvine.stocks.model.StockDetails;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class StockManager {

    private Map<String, List<StockDetails>> stockTickerToAmountOwned = new HashMap<>();

    private static String formatLookupString(String stringToFormat) {
        if (stringToFormat != null) {
            stringToFormat = stringToFormat.replaceAll("\\s", "").toUpperCase();
        }

        return stringToFormat;
    }

    /**
     * Get the list of stocks currently under management by its ticker symbol.
     */
    public List<Stock> findByTicker(final String ticker) {
        String lookupTicker = formatLookupString(ticker);

        List<StockDetails> stockDetails = stockTickerToAmountOwned.getOrDefault(lookupTicker, new ArrayList<>());
        return stockDetails.stream().map(StockDetails::getStock).collect(Collectors.toList());
    }

    /**
     * For a given ticker symbol, add up the total stock values under management and return.
     */
    public BigDecimal getValueUnderManagerByTicker(final String ticker) {
        List<StockDetails> details = stockTickerToAmountOwned.get(formatLookupString(ticker));
        if (details == null) {
            return new BigDecimal(0);
        }
        return details.stream().map(d -> {
            BigDecimal multiply = d.getStock().getPrice().multiply(new BigDecimal(d.getSharesOwned()));
            return multiply;
        }).reduce(BigDecimal::add).get().stripTrailingZeros();
    }

    /**
     * For a given ticker symbol, get the number of stocks under management.
     */
    public int numberOfStocksByTicker(final String ticker) {
        List<StockDetails> details = stockTickerToAmountOwned.get(formatLookupString(ticker));
        if (details == null) {
            return 0;
        }
        return details.stream().map(StockDetails::getSharesOwned).reduce(Integer::sum).get();
    }

    /**
     * Add a new stock to be managed by our system (here 'buy' is equal to 'add').
     *
     * @param stock          - The {@link com.mapvine.stocks.model.Stock} to buy
     * @param numberOfShares - The number of shares to purchase
     * @throws java.lang.IllegalArgumentException if numberOfShares is <= 0
     */
    public void buyStock(final Stock stock, final int numberOfShares) {
        if (numberOfShares <= 0) {
            throw new IllegalArgumentException("Invalid number of stocks to buy");
        }
        String ticker = stock.getTicker();
        List<StockDetails> stockDetails = stockTickerToAmountOwned.get(ticker);
        if (stockDetails == null) {
            List<StockDetails> newStockDetails = new ArrayList<>();
            newStockDetails.add(new StockDetails(stock, numberOfShares));
            stockTickerToAmountOwned.put(ticker,  newStockDetails);
        } else {
            stockDetails.add(new StockDetails(stock, numberOfShares));
        }
    }

    /**
     * For a given ticker, sell the stock. The stock sold should be returned before being removed from management.
     * <p>
     * This method will attempt to sell stock starting with the lowest price.
     *
     * @param ticker         - The stock ticker to sell
     * @param numberOfShares - The number of shares to purchase
     * @param sharePrice     - The price we are selling numberOfShares
     * @return A list of the stock prices that were sold sorted from most to least expensive
     * @throws java.lang.IllegalArgumentException if numberOfShares is <= 0
     */
    public Optional<Set<BigDecimal>> sellStock(final String ticker, final int numberOfShares, final BigDecimal sharePrice) {
        if (numberOfShares <= 0) {
            throw new IllegalArgumentException("Invalid number of stocks to buy");
        }

        List<StockDetails> details = stockTickerToAmountOwned.get(formatLookupString(ticker));
        if (details == null) {
            return Optional.empty();
        }
        Collections.sort(details, new Comparator<StockDetails>() {
            @Override
            public int compare(StockDetails o1, StockDetails o2) {
                return o1.getStock().getPrice().compareTo(o2.getStock().getPrice());
            }
        });
        Set<BigDecimal> sellPrices = new HashSet<>();
        int numberOfSharesSatisfied = 0;
        for (StockDetails detail : details) {
            int sharesThatNeedToBeFilled = numberOfShares - numberOfSharesSatisfied;
            int numberOfSharesOwned = detail.getSharesOwned();
            if (sharesThatNeedToBeFilled >= numberOfSharesOwned) {
                numberOfSharesSatisfied += numberOfSharesOwned;
                detail.setSellDetails(new SellDetails(sharePrice, numberOfSharesOwned));
            } else {
                detail.setSellDetails(new SellDetails(sharePrice, sharesThatNeedToBeFilled));
            }
            sellPrices.add(detail.getStock().getPrice());
        }
        Optional<Set<BigDecimal>> retVal = Optional.of(sellPrices);
        retVal.ifPresent(d -> {
            for (StockDetails detail : details) {
                if (detail.getSellDetails() != null) {
                    int sharesSold = detail.getSellDetails().getSharesToSell();
                    int sharesAfterSell = detail.getSharesOwned() - sharesSold;
                    detail.setSharesOwned(sharesAfterSell);
                    BigDecimal amountFromSale = detail.getSellDetails().getSellPrice().multiply(new BigDecimal(sharesSold));
                    BigDecimal value = detail.getStock().getPrice().multiply(new BigDecimal(sharesSold));
                    detail.setProfitLoss(amountFromSale.subtract(value));
                    detail.setSellDetails(null);
                }
            }
        });
        return retVal;
    }

    /**
     * For a given stock ticker, get the P&L.
     *
     * @return The total profit made so far or an empty option if the stock is not under management
     */
    public Optional<BigDecimal> getProfitForStockByTicker(final String ticker) {
        List<StockDetails> details = stockTickerToAmountOwned.get(formatLookupString(ticker));
        if (details == null) {
            return Optional.empty();
        }
        return Optional.of(details.stream().map(StockDetails::getProfitLoss).reduce(BigDecimal::add).get());
    }
}
