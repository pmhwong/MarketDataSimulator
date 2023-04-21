package org.philip;

import com.lmax.disruptor.RingBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Build a market data generator that generates prices for a symbol
 */
public class MarketDataGenerator implements IMarketDataGenerator {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final int numQuotes;
    private final Random random;
    private final String symbol;
    private final RingBuffer<Quote> ringBuffer;

    protected final Map<String, Double> lastPriceMap = new HashMap<>() {
        {
            put("0700.HK", 360.0);
            put("1211.HK", 229.0);
            put("9888.HK", 126.3);
        }
    };

    public MarketDataGenerator(String symbol, int numQuotes, RingBuffer<Quote> ringBuffer) {
        if (lastPriceMap.get(symbol) == null) {
            throw new IllegalArgumentException("Symbol not found.");
        }
        this.random = new Random();
        this.symbol = symbol;
        this.numQuotes = numQuotes;
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void start() {
        executorService.submit(() -> {
            double price = lastPriceMap.get(symbol);
            for (int i=0 ; i < numQuotes ; i++) {
                int nextTick = random.nextInt(3) - 1;
                price += nextTick * 0.1;
                long sequenceId = ringBuffer.next();
                Quote quote = ringBuffer.get(sequenceId);
                quote.setSymbol(symbol);
                quote.setBestBid(price);
                quote.setBestAsk(price + 0.1);
                ringBuffer.publish(sequenceId);
            }
        });
    }

    @Override
    public void shutdown() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch(Exception e) {}
    }
}
