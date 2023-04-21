package org.philip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class MarketDataSimulator {

    public static void main(String[] args) throws Exception {
        int numQuotes = 20_000;

        try {
            IMarketDataProvider marketDataProvider = new MarketDataProvider(new String[] {"1211.HK", "0700.HK"}, numQuotes);
            IMarketDataService marketDataService = new MarketDataService(marketDataProvider);
            IMarketDataConsumer consumer1 = new MarketDataConsumer(marketDataService, "1211.HK");
            IMarketDataConsumer consumer2 = new MarketDataConsumer(marketDataService, "0700.HK");

            consumer1.start();
            consumer2.start();
            marketDataService.start();
            marketDataService.shutdown();

            LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
            loggerContext.stop();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
