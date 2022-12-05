package vip.linhs.stock.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import vip.linhs.stock.model.po.DailyIndex;
import vip.linhs.stock.model.po.StockInfo;
import vip.linhs.stock.parser.DailyIndexParser;
import vip.linhs.stock.parser.StockInfoParser;
import vip.linhs.stock.parser.StockInfoParser.EmStock;
import vip.linhs.stock.service.StockCrawlerService;
import vip.linhs.stock.util.HttpUtil;
import vip.linhs.stock.util.StockUtil;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockCrawlerServiceImpl implements StockCrawlerService {
    private static final Logger logger = LoggerFactory.getLogger(StockCrawlerServiceImpl.class);

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    @Qualifier("eastmoneyStockInfoParser")
    private StockInfoParser stockInfoParser;

    @Autowired
    private DailyIndexParser dailyIndexParser;

    @Override
    public List<StockInfo> getStockList() {
        List<EmStock> list = getStockList("f12,f13,f14");
        list.forEach(v -> v.getStockInfo().setAbbreviation(StockUtil.getPinyin(v.getStockInfo().getName())));
        return list.stream().map(EmStock::getStockInfo).collect(Collectors.toList());
    }

    private List<EmStock> getStockList(String fields) {
        String content = HttpUtil.sendGet(httpClient, "http://20.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=10000000&np=1&fid=f3&fields=" + fields + "&fs=m:0+t:6,m:0+t:13,m:0+t:80,m:0+t:81+s:2048,m:1+t:2,m:1+t:23,b:MK0021,b:MK0022,b:MK0023,b:MK0024");
        if (content != null) {
            List<EmStock> list = stockInfoParser.parseStockInfoList(content);
            list = list.stream().filter(v -> v.getStockInfo().getExchange() != null).collect(Collectors.toList());
            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public DailyIndex getDailyIndex(String code) {
        List<DailyIndex> dailyIndexList = getDailyIndex(Arrays.asList(code));
        return dailyIndexList.isEmpty() ? null : dailyIndexList.get(0);
    }

    @Override
    public List<DailyIndex> getDailyIndex(List<String> codeList) {
        String codes = codeList.stream().map(StockUtil::getFullCode).collect(Collectors.joining(","));
        HashMap<String, String> header = new HashMap<>();
        header.put("Referer", "https://finance.sina.com.cn/");
        String content = HttpUtil.sendGet(httpClient, "https://hq.sinajs.cn/list=" + codes, header, "gbk");
        if (content != null) {
            return dailyIndexParser.parseDailyIndexList(content);
        }
        return Collections.emptyList();
    }

    @Override
    public List<DailyIndex> getDailyIndexFromEastMoney() {
        List<EmStock> list = getStockList("f2,f5,f6,f8,f12,f13,f15,f16,f17,f18");
        return list.stream().map(EmStock::getDailyIndex).collect(Collectors.toList());
    }


    @Override
    public List<DailyIndex> getHistoryDailyIndexs(String code) {
        String content = getHistoryDailyIndexsString(code);
        if (content != null) {
            return dailyIndexParser.parseHistoryDailyIndexList(content);
        }
        return Collections.emptyList();
    }

    @Override
    public String getHistoryDailyIndexsString(String code) {
        logger.info("http://www.aigaogao.com/tools/history.html?s=" + code, "gbk");
        return HttpUtil.sendGet(httpClient, "http://www.aigaogao.com/tools/history.html?s=" + code, "gbk");
    }

    @Override
    public String getHistoryDailyIndexsStringFrom163(String code, int year, int season) {
        logger.info(String.format("https://quotes.money.163.com/trade/lsjysj_%s.html?year=%d&season=%d", code, year, season));
        return HttpUtil.sendGet(httpClient, String.format("https://quotes.money.163.com/trade/lsjysj_%s.html?year=%d&season=%d", code, year, season));
    }


    //public static String sendPost(CloseableHttpClient httpClient, String url, Map<String, Object> params, Map<String, String> header)
//https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH000001&begin=1670247216429&period=day&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance
        @Override
    public String getHistoryDailyIndexsStringFromXueQiu(String code, int day) {

            Map<String, String> header = new HashMap<>();
            header.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            header.put("cookie", "device_id=6843aff8e6949458ff0296b588d0f280; xq_a_token=df4b782b118f7f9cabab6989b39a24cb04685f95; xqat=df4b782b118f7f9cabab6989b39a24cb04685f95; xq_r_token=3ae1ada2a33de0f698daa53fb4e1b61edf335952; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY3MjE4Njc1MSwiY3RtIjoxNjcwMTY0NTA3MTAzLCJjaWQiOiJkOWQwbjRBWnVwIn0.QkTb4BDzdwQ_Y4dZFhfPWJMXwzVjEH6KM6K9TyuE6mndK_HWuHxXDJWwClUixJgXGacK0uEVDuWs_TifIVbJN0PS6Vw0-eHWjOsMuzWEJhSq-G60eZDYD2HE_6p3ZuhYe9Sd-pj71lxM1qg4W0UcG896VVOURahsC16XTpWtS61rAu-kSqgkxHrv3t7njHc9VqpdLfBw-dlvzOS9VirqktEKluOFIzht1679Ia4R_PRNEsV_LGZydzvUWCdmoM7iJpXE6bcEMrZcklHyXpyQDqnIBGLzIaUhM-Uvl1L0A2Od-n6DaYnihvndhh0Kohqi94SJrIv4jeSPUv87cp_KHA; u=291670164562230; Hm_lvt_1db88642e346389874251b5a1eded6e3=1668578158,1668776329,1669182895,1670164563; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1670164637");
            header.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.68");

            return HttpUtil.sendGet(httpClient, "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol="+code+"&begin="+System.currentTimeMillis()+"&period=day&type=before&count=-230&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance", header);
    }

}
