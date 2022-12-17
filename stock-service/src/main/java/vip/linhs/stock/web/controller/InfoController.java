//package vip.linhs.stock.web.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import org.apache.http.HttpEntity;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@RestController
//@RequestMapping("hotmap")
//public class InfoController {
//    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);
//
//
//    @GetMapping("/query")
//    public List<Map<String, List<String>>> queryTdx(@RequestParam(value = "time", required = false) String time) throws ParseException, IOException {
//
//        logger.info("date->{} ", time);
//        List<Map<String, List<String>>> result=new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Calendar calendar=Calendar.getInstance();
//            calendar.setTime(new Date());
//            System.out.println(calendar.get(Calendar.DAY_OF_MONTH));//今天的日期
//            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+i);//让日期加1
//            System.out.println(calendar.get(Calendar.DATE));//加1之后的日期Top
//
//            String thisDate = format(calendar.getTime().toString());
//            List<Map<String, List<String>>> list = excuseUrlAndParse2(thisDate);
//            mapMerge(result, list);
//        }
//        Collections.sort(result, new Comparator<Map<String, List<String>>>() {
//            @Override
//            public int compare(Map<String, List<String>> o1, Map<String, List<String>> o2) {
//                List<String> list1 = (List<String>) o1.values().toArray()[0];
//                List<String> list2 = (List<String>) o2.values().toArray()[0];
//
//                return list2.size() - list1.size();
//            }
//        });
//
//        remove(result, "ST股");
//        remove(result, "公告");
//        remove(result, "业绩预增");
//        remove(result, "次新股");
//        remove(result, "其他");
//
//        return result;
//    }
//
//
//    @GetMapping("/queryinfo")
//    public List<Map<String, List<String>>> queryJys(@RequestParam(value = "time", required = false) String time) throws IOException {
//        List<Map<String, List<String>>> result = new ArrayList<>();
//        for (String date1 : dateList) {
//            for (int i = 0; i < 10; i++) {
//                Calendar calendar=Calendar.getInstance();
//                calendar.setTime(new Date());
//                System.out.println(calendar.get(Calendar.DAY_OF_MONTH));//今天的日期
//                calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+i);//让日期加1
//                System.out.println(calendar.get(Calendar.DATE));//加1之后的日期Top
//
//            List<Map<String, List<String>>> list = excuseJiuyangongshe(date1);
//            mapMerge(result, list);
//        }
//
//        Collections.sort(result, new Comparator<Map<String, List<String>>>() {
//            @Override
//            public int compare(Map<String, List<String>> o1, Map<String, List<String>> o2) {
//                List<String> list1 = (List<String>) o1.values().toArray()[0];
//                List<String> list2 = (List<String>) o2.values().toArray()[0];
//
//                return list2.size() - list1.size();
//            }
//        });
//
//        remove(result, "ST板块");
//        remove(result, "公告");
//        remove(result, "业绩预增");
//        remove(result, "新股");
//        remove(result, "其他");
//        return result;
//    }
//
//
//    public static String format(String date) throws ParseException {
//
//        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//        //将indate转化为Date类型
//        Date result = df2.parse(date);
//        String format = df.format(result);
//        return format;
//    }
//
//
//    public static void remove(List<Map<String, List<String>>> list, String element) {
//        Iterator<Map<String, List<String>>> iterator = list.iterator();
//
//        while (iterator.hasNext()) {
//            if (iterator.next().keySet().iterator().next().equals(element)) {
//                iterator.remove();
//            }
//        }
//    }
//
//    private static List<Map<String, List<String>>> excuseUrlAndParse2(String date) throws IOException {
//        String url = "http://ddx.gubit.cn/jiedu/?date=" + date;
//        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//
//        HttpGet httpGet = new HttpGet(url);
//        // 响应模型
//        CloseableHttpResponse response = null;
//
//        // 由客户端执行(发送)Post请求
//        response = httpClient.execute(httpGet);
//        // 从响应模型中获取响应实体
//        HttpEntity responseEntity = response.getEntity();
//        String str = EntityUtils.toString(responseEntity);
//        Document doc = Jsoup.parse(str);
//
//
//        List list = new ArrayList();
//        Elements header = doc.getElementsByClass("header");
//
//        for (int i = 0; i < header.size(); i++) {
//            String key = header.get(i).children().get(0).text();
//
//            // 所有#id的标签
//            Elements elements = doc.select("tbody").get(i).children();
//
//
//            List<String> codes = new ArrayList<String>();
//            for (Element element : elements) {
//
//                String text = element.children().get(0).children().get(0).children().get(2).text();
//
//                codes.add(text);
//            }
//            Map<String, List<String>> result = new HashMap<>();
//            result.put(key, codes);
//            list.add(result);
//        }
//
//
//        return list;
//
//
//    }
//
//
//    private static List<Map<String, List<String>>> excuseJiuyangongshe(String dateStr) throws IOException {
//        String url = "https://app.jiuyangongshe.com/jystock-app/api/v1/action/field";
//        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//
//        HttpPost httppost = new HttpPost(url); //建立HttpPost对象
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        httppost.addHeader("content-type", "application/json");
//        httppost.addHeader("platform", "3");
//        httppost.addHeader("timestamp", "1669541594733");
//        httppost.addHeader("token", "90241383e7499d6609ce94a38cdfafa7");
//
//
////建立一个NameValuePair数组，用于存储欲传送的参数
//
//        JSONObject param2 = new JSONObject();
//
//        param2.put("date", dateStr);
//
//        param2.put("pc", "1");
//
//
//        StringEntity stringEntity = new StringEntity(param2.toString());
//
//        httppost.setEntity(stringEntity);
//
//        // 响应模型
//        CloseableHttpResponse response = null;
//
//        // 由客户端执行(发送)Post请求
//        response = httpClient.execute(httppost);
//        // 从响应模型中获取响应实体
//        HttpEntity responseEntity = response.getEntity();
//        String str = EntityUtils.toString(responseEntity);
//        JSONObject dataJson = JSONObject.parseObject(str);
//
//        List list = new ArrayList();
//
//
//        for (int i = 1; i < dataJson.getJSONArray("data").size(); i++) {
//            JSONObject temp = JSONObject.parseObject(dataJson.getJSONArray("data").get(i).toString());
//            String key = temp.getString("name");
//
//
//            List<String> codes = new ArrayList<String>();
//            for (Object element : temp.getJSONArray("list")) {
//                JSONObject e = JSONObject.parseObject(element.toString());
//                String text = e.getString("code");
//
//                codes.add(text);
//            }
//            Map<String, List<String>> result = new HashMap<>();
//            result.put(key, codes);
//            list.add(result);
//        }
//
//
//        return list;
//
//
//    }
//
//
//    public static synchronized List<Map<String, List<String>>> mapMerge(List<Map<String, List<String>>> mapOne, List<Map<String, List<String>>> mapTwo) {
//        for (Map<String, List<String>> map : mapOne) {
//            for (Map<String, List<String>> listMap : mapTwo) {
//                String key1 = map.keySet().iterator().next();
//                String key2 = listMap.keySet().iterator().next();
//                if (key1.equals(key2)) {
//                    List<String> key1Value = map.get(key1);
//                    List<String> key2Value = listMap.get(key1);
//                    key1Value.addAll(key2Value);
//                    map.put(key1, key1Value);
//                }
//            }
//        }
//        List<String> list = new ArrayList<>();
//        for (Map<String, List<String>> map : mapOne) {
//            String key = map.keySet().iterator().next();
//            list.add(key);
//        }
//        List<String> list2 = new ArrayList<>();
//        for (Map<String, List<String>> map : mapTwo) {
//            String key = map.keySet().iterator().next();
//            list2.add(key);
//        }
//
//        List<String> list3 = getDiffrent(list, list2);
//        for (Map<String, List<String>> map : mapTwo) {
//            String key = map.keySet().iterator().next();
//            if (list3.contains(key)) {
//                mapOne.add(map);
//            }
//        }
//        return mapOne;
//    }
//
//    /**
//     * 获取两个List的不同元素
//     *
//     * @param list1
//     * @param list2
//     * @return
//     */
//    private static List<String> getDiffrent(List<String> list1, List<String> list2) {
//
//        List<String> diff = new ArrayList<String>();
//        if (list1.size() > list2.size()) {
//            for (String str : list1) {
//                if (!list2.contains(str)) {
//                    diff.add(str);
//                }
//            }
//        } else {
//            for (String str : list2) {
//                if (!list1.contains(str)) {
//                    diff.add(str);
//                }
//            }
//        }
//
//        return diff;
//    }
//}
