package vip.linhs.stock.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import vip.linhs.stock.util.StockConsts;
import vip.linhs.stock.util.StockUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public List<StockInfo> getZTfromddxgubitcn(String dateStr) {
        String url = "http://ddx.gubit.cn/jiedu/?date=" + dateStr;
        String body = HttpUtil.sendGet(httpClient, url);
        Document doc = Jsoup.parse(body);

        List<StockInfo> list = new ArrayList();
        Elements header = doc.getElementsByClass("header");

        for (int i = 0; i < header.size(); i++) {
            String key = header.get(i).children().get(0).text();
            if (key.contains("公告") || key.contains("其他") || key.contains("ST股")||key.contains("业绩预增")) {
                continue;
            }
            // 所有#id的标签
            Elements elements = doc.select("tbody").get(i).children();
            for (Element element : elements) {
                StockInfo stockInfo = new StockInfo();
                String text = element.children().get(0).children().get(0).children().get(2).text();
                stockInfo.setName(element.children().get(0).children().get(0).children().get(0).text());
                stockInfo.setCode(text.split("\\.")[0]);
                stockInfo.setExchange(text.split("\\.")[1]);
                stockInfo.setTag(key.equals("大金融") ? "金融" : key.equals("大消费") ? "消费" : key.equals("汽车零部件") ? "汽车产业链" :
                        key.equals("半导体产业链") ? "国产芯片" : key.equals("医药医疗")?"医药":key.equals("优化生育（三孩）")?"三胎":key.equals("新能源汽车")?"汽车零部件":key);
                stockInfo.setType(StockConsts.StockZTType.ChaGuWang.value());
                try {
                    Date date = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
                    date = DateUtils.parseDate(new SimpleDateFormat("yyyy-MM-dd").format(date), "yyyy-MM-dd");
                    stockInfo.setCreateTime(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                list.add(stockInfo);
            }
        }
        return list;
    }

    public List<StockInfo> getZTfromjiuyangongshe(String dateStr) {

        String url = "https://app.jiuyangongshe.com/jystock-app/api/v1/action/field";
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost httppost = new HttpPost(url); //建立HttpPost对象
        httppost.addHeader("content-type", "application/json");
        httppost.addHeader("platform", "3");
        httppost.addHeader("timestamp", "1674908485669");
        httppost.addHeader("token", "13c4e84197dcba3a46e77a6f521a774e");

        JSONObject param2 = new JSONObject();
        param2.put("date", dateStr);
        param2.put("pc", "1");

        StringEntity stringEntity = null;
        CloseableHttpResponse response = null;
        JSONObject dataJson = null;
        try {
            stringEntity = new StringEntity(param2.toString());
            httppost.setEntity(stringEntity);
            response = httpClient.execute(httppost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            String str = EntityUtils.toString(responseEntity);
            dataJson = JSONObject.parseObject(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<StockInfo> list = new ArrayList();
        for (int i = 1; i < dataJson.getJSONArray("data").size(); i++) {
            JSONObject temp = JSONObject.parseObject(dataJson.getJSONArray("data").get(i).toString());
            String key = temp.getString("name");
            if (key.contains("公告") || key.contains("新股")|| key.contains("年报披露")||key.contains("其他") || key.contains("ST板块")||key.contains("业绩预增")) {
                continue;
            }
            for (Object element : temp.getJSONArray("list")) {
                StockInfo stockInfo = new StockInfo();
                JSONObject e = JSONObject.parseObject(element.toString());
                String code = e.getString("code");

                stockInfo.setName(e.getString("name"));
                stockInfo.setCode(code.substring(2));
                stockInfo.setExchange(code.substring(0,2));
                stockInfo.setTag(key);
                stockInfo.setType(StockConsts.StockZTType.JiuYanGoneShe.value());
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                    stockInfo.setCreateTime(date);
                } catch (ParseException parseException) {
                    throw new RuntimeException(parseException);
                }
                list.add(stockInfo);
            }
        }


        return list;
}
}
