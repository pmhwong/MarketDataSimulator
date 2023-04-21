package org.philip;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketDataService implements IMarketDataService {
    private static final Logger logger = LogManager.getLogger(MarketDataService.class);
    private static final WaitStrategy waitStrategy = new BusySpinWaitStrategy();
    private final Disruptor<IQuote> disruptor;
    private final RingBuffer<IQuote> ringBuffer;
    private final IMarketDataProvider marketDataProvider;
    private final Map<String, List<IEventHandler<IQuote>>> subscribersMap;

    public MarketDataService(IMarketDataProvider marketDataProvider) {
        subscribersMap = new HashMap<>();
        disruptor = new Disruptor<>(
                Quote::new,
                128,
                DaemonThreadFactory.INSTANCE,
                ProducerType.MULTI,
                waitStrategy);
        disruptor.handleEventsWith(this);
        ringBuffer = disruptor.getRingBuffer();
        this.marketDataProvider = marketDataProvider;
    }

    @Override
    public boolean subscribeQuotes(String symbol, IEventHandler<IQuote> subscriber) {
        marketDataProvider.subscribe(symbol, this);
        List<IEventHandler<IQuote>> subscribers = subscribersMap.getOrDefault(symbol, new ArrayList<>());
        subscribersMap.putIfAbsent(symbol, subscribers);
        return subscribers.add(subscriber);
    }

    /**
     * Receive and process a quote from the market data feed using the ring buffer instance.
     */
    @Override
    public void onEvent(IQuote quote) {
        long sequenceId = ringBuffer.next();
        IQuote quoteEvent = ringBuffer.get(sequenceId);
        quoteEvent.setQuote(quote);
        ringBuffer.publish(sequenceId);
    }

    /**
     * Consume an event that has been published by the ring buffer.
     * For order events, this event should be dispatched to another queue to be
     * handled by a thread pool that ensures sequential processing per order.
     */
    @Override
    public void onEvent(IQuote quote, long sequence, boolean endOfBatch) throws Exception {
        List<IEventHandler<IQuote>> subscribers = subscribersMap.get(quote.getSymbol());
        for (IEventHandler<IQuote> subscriber : subscribers) {
            subscriber.onEvent(quote);
        }
    }

    @Override
    public void start() {
        disruptor.start();
        marketDataProvider.start();
    }

    @Override
    public void shutdown() {
        marketDataProvider.shutdown();
        disruptor.shutdown();
    }
}
