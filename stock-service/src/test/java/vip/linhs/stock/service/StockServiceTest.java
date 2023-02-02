package vip.linhs.stock.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vip.linhs.stock.service.impl.TaskServiceImpl;
import vip.linhs.stock.util.HttpUtil;
import vip.linhs.stock.web.controller.ReportController;

@SpringBootTest
public class StockServiceTest {

    @Autowired
    private StockService stockService;
    @Autowired
    private TaskServiceImpl taskService;
    @Autowired
    private ReportController reportController;

    @Test
    public void testSaveDailyIndexToFile() {
        String rootPath = "/logs";
        stockService.saveDailyIndexToFile(rootPath);
    }

    @Test
    public void testSaveDailyIndexFromFile() {
        String rootPath = "/logs";
        stockService.saveDailyIndexFromFile(rootPath);
    }

    @Test
    public void testFixDailyIndex() {
        stockService.fixDailyIndex(202201, null);
//        stockService.fixDailyIndex(202201, Arrays.asList("300059"));
    }

    @Test
    public void testupdateStockZt(){
        taskService.updateStockZt();
//        String dateStr="20230120,20230119,20230118,20230117,20230116,20230113,20230112,20230111,20230110,20230109,20230106,20230105,20230104,20230103,20221230,20221229,20221228,20221227,20221226,20221223,20221222,20221221,20221220,20221219,20221216,20221215,20221214,20221213,20221212,20221209,20221208,20221207,20221206,20221205,20221202,20221201,20221130,20221129,20221128,20221125,20221124,20221123,20221122,20221121,20221118,20221117,20221116,20221115,20221114,20221111,20221110,20221109,20221108,20221107,20221104,20221103,20221102,20221101,20221031,20221028,20221027,20221026,20221025,20221024,20221021,20221020,20221019,20221018,20221017,20221014,20221013,20221012,20221011,20221010,20220930,20220929,20220928,20220927,20220926,20220923,20220922,20220921,20220920,20220919,20220916,20220915,20220914,20220913,20220909,20220908,20220907,20220906,20220905,20220902,20220901,20220831,20220830,20220829,20220826,20220825,20220824,20220823,20220822,20220819,20220818,20220817,20220816,20220815,20220812,20220811,20220810,20220809,20220808,20220805,20220804,20220803,20220802,20220801,20220729,20220728,20220727,20220726,20220725,20220722,20220721,20220720,20220719,20220718,20220715,20220714,20220713,20220712,20220711,20220708,20220707,20220706,20220705,20220704,20220701,20220630,20220629,20220628,20220627,20220624,20220623,20220622,20220621,20220620,20220617,20220616,20220615,20220614,20220613,20220610,20220609,20220608,20220607,20220606,20220602,20220601,20220531,20220530,20220527,20220526,20220525,20220524,20220523,20220520,20220519,20220518,20220517,20220516,20220513,20220512,20220511,20220510,20220509,20220506,20220505,20220429,20220428,20220427,20220426,20220425,20220422,20220421,20220420,20220419,20220418,20220415,20220414,20220413,20220412,20220411,20220408,20220407,20220406,20220401,20220331,20220330,20220329,20220328,20220325,20220324,20220323,20220322,20220321,20220318,20220317,20220316,20220315,20220314,20220311,20220310,20220309,20220308,20220307,20220304,20220303,20220302,20220301,20220228,20220225,20220224,20220223,20220222,20220221,20220218,20220217,20220216,20220215,20220214,20220211,20220210,20220209,20220208,20220207,20220128,20220127,20220126,20220125,20220124,20220121,20220120,20220119,20220118,20220117,20220114,20220113,20220112,20220111,20220110,20220107,20220106,20220105,20220104";
//        for(String date:dateStr.split(",")) {
//            taskService.updateStockZt(date);
//        }
    }

    @Autowired
    private CloseableHttpClient httpClient;
    @Test
    public void testGaokai(){
        //getTyphoonReport();
        String body = HttpUtil.sendGet(httpClient, "https://www.jiuyangongshe.com/action/2022-11-21");
        for(int i=1 ;i<body.split(",\\{action_field_id:").length;i++){
            String one=body.split(",\\{action_field_id:")[i];
            System.out.println("==="+one);

        }
    }

    public void getTyphoonReport() {
        String typhoonWeatherUrl="https://www.jiuyangongshe.com/action/2022-11-02";
        try {
            WebClient wc = new WebClient(BrowserVersion.CHROME);
            // 启用JS解释器，默认为true
            wc.getOptions().setJavaScriptEnabled(true);
            // 禁用css支持
            wc.getOptions().setCssEnabled(false);
            // js运行错误时，是否抛出异常
            wc.getOptions().setThrowExceptionOnScriptError(false);
            // 状态码错误时，是否抛出异常
            wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
            // 设置连接超时时间 ，这里是5S。如果为0，则无限期等待
            wc.getOptions().setTimeout(5000);
            // 是否允许使用ActiveX
            wc.getOptions().setActiveXNative(false);
            // 等待js时间
            wc.waitForBackgroundJavaScript(1 * 3000);
            // 设置Ajax异步处理控制器即启用Ajax支持
            wc.setAjaxController(new NicelyResynchronizingAjaxController());
            // 不跟踪抓取
            wc.getOptions().setDoNotTrackEnabled(false);
            HtmlPage page = wc.getPage(typhoonWeatherUrl);
            // 以xml的形式获取响应文本
            String pageXml = page.asXml();
            System.out.println(pageXml);
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public static void main(String[] args) {


    }
}
