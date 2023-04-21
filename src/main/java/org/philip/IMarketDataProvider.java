package org.philip;

public interface IMarketDataProvider {
    boolean subscribe(String symbol, IEventHandler<IQuote> eventHandler);

    void start();

    void shutdown();
}
