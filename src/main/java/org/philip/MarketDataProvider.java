package org.philip;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketDataProvider implements IMarketDataProvider, EventHandler<IQuote> {
    private final Map<String, IMarketDataGenerator> marketDataGeneratorsMap;
    private final Map<String, List<IEventHandler<IQuote>>> subscribersMap;
    private final Disruptor<Quote> disruptor;
    private final RingBuffer<Quote> ringBuffer;

    public MarketDataProvider(String[] symbols, int numQuotes) {
        disruptor = new Disruptor<>(
                Quote::new,
                128,
                DaemonThreadFactory.INSTANCE,
                ProducerType.MULTI,
                new BusySpinWaitStrategy());
        disruptor.handleEventsWith(this);
        ringBuffer = disruptor.getRingBuffer();
        marketDataGeneratorsMap = new HashMap<>();
        for (String symbol : symbols) {
            if (marketDataGeneratorsMap.containsKey(symbol)) continue;
            IMarketDataGenerator marketDataGenerator = new MarketDataGenerator(symbol, numQuotes, ringBuffer);
            marketDataGeneratorsMap.putIfAbsent(symbol, marketDataGenerator);
        }
        subscribersMap = new HashMap<>();
    }

    @Override
    public boolean subscribe(String symbol, IEventHandler<IQuote> eventHandler) {
        List<IEventHandler<IQuote>> subscribers = subscribersMap.getOrDefault(symbol, new ArrayList<>());
        subscribersMap.putIfAbsent(symbol, subscribers);
        return subscribers.add(eventHandler);
    }

    @Override
    public void onEvent(IQuote quote, long sequence, boolean endOfBatch) throws Exception {
        notifySubscribers(quote);
    }

    private void notifySubscribers(IQuote quote) {
        for (IEventHandler<IQuote> subscriber : subscribersMap.get(quote.getSymbol())) {
            subscriber.onEvent(quote);
        }
    }

    @Override
    public void start() {
        disruptor.start();
        for (IMarketDataGenerator marketDataGenerator : marketDataGeneratorsMap.values()) {
            marketDataGenerator.start();
        }
    }

    @Override
    public void shutdown() {
        for (IMarketDataGenerator marketDataGenerator : marketDataGeneratorsMap.values()) {
            marketDataGenerator.shutdown();
        }
        disruptor.shutdown();
    }
}
