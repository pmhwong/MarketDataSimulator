package org.philip;

public interface IQuote {
    void setQuote(IQuote quote);

    String getSymbol();

    double getBestBid();

    double getBestAsk();
}
