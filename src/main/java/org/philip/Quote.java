package org.philip;

public class Quote implements IQuote, Cloneable {
    private String symbol;
    private double bestBid;
    private double bestAsk;

    public Quote() {
    }

    public Quote(String symbol, double bestBid, double bestAsk) {
        this.symbol = symbol;
        this.bestBid = bestBid;
        this.bestAsk = bestAsk;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBestBid() {
        return bestBid;
    }

    public void setBestBid(double bestBid) {
        this.bestBid = bestBid;
    }

    public double getBestAsk() {
        return bestAsk;
    }

    public void setBestAsk(double bestAsk) {
        this.bestAsk = bestAsk;
    }

    @Override
    public Quote clone() {
        try {
            Quote clone = (Quote) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void setQuote(IQuote quote) {
        setSymbol(quote.getSymbol());
        setBestBid(quote.getBestBid());
        setBestAsk(quote.getBestAsk());
    }

    @Override
    public String toString() {
        return "Quote{" +
                "symbol='" + symbol + '\'' +
                ", bestBid=" + bestBid +
                ", bestAsk=" + bestAsk +
                '}';
    }
}
