package org.philip;

import com.lmax.disruptor.EventHandler;

public interface IMarketDataService extends IEventHandler<IQuote>, EventHandler<IQuote> {
    boolean subscribeQuotes(String symbol, IEventHandler<IQuote> subscriber);

    void start();

    void shutdown();
}
