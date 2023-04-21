package org.philip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Build a multi-thread consumer that subscribes to market data for a symbol
 * and prints out the price update as quickly as possible.
 * Order of consumption of the market data is not important.
 */
public class MarketDataConsumer implements IMarketDataConsumer, IEventHandler<IQuote> {
    private static final Logger logger = LogManager.getLogger(MarketDataService.class);
    private final IMarketDataService marketDataService;
    private final String symbol;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(8);
    private final Queue<IQuote> quoteQueue = new ArrayDeque<>();

    public MarketDataConsumer(IMarketDataService marketDataService, String symbol) {
        this.marketDataService = marketDataService;
        this.symbol = symbol;
    }

    @Override
    public void onEvent(IQuote quote) {
        logger.info("time=" + (new Date()).getTime() + ", quote: " + quote);
    }

    @Override
    public void start() {
        marketDataService.subscribeQuotes(symbol, this);
    }
}
