package vip.linhs.stock.service;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import vip.linhs.stock.model.po.DailyIndex;
import vip.linhs.stock.model.po.StockInfo;

@SpringBootTest
public class StockCrawlerServiceTest {

    @Autowired
    private StockCrawlerService stockCrawlerService;

    @Test
    public void testGetStockList() {
        List<StockInfo> stockList = stockCrawlerService.getStockList();
        Assertions.assertNotNull(stockList);
        Assertions.assertFalse(stockList.isEmpty());
    }

    @Test
    public void testGetDailyIndex() {
        DailyIndex dailyIndex = stockCrawlerService.getDailyIndex("000001");
        Assertions.assertNotNull(dailyIndex);

        dailyIndex = stockCrawlerService.getDailyIndex("sz000001");
        Assertions.assertNotNull(dailyIndex);

        dailyIndex = stockCrawlerService.getDailyIndex("sh000001");
        Assertions.assertNotNull(dailyIndex);
    }

    @Test
    public void testGetHistoryDailyIndexs() {
        // List<DailyIndex> list = stockCrawlerService.getHistoryDailyIndexs("300542");
        List<DailyIndex> list = stockCrawlerService.getHistoryDailyIndexs("688580");
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testZT() {
        stockCrawlerService.getZTfromddxgubitcn("20230120");
    }


    public static void main(String[] args) {
        String t ="com.framework.redis.aaa.mmm";
        String[] split = t.split(".");
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
        }
        String m ="123.123.123" ;
        String[] split1 = m.split("\\.");
        Arrays.stream(split1).forEach(System.out::println);

        System.out.println(t.indexOf("."));
    }
}
