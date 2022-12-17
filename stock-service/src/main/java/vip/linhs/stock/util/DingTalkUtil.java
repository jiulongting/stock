package vip.linhs.stock.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 钉钉机器人工具
 * @author: Mr.Zheng
 * @email: securityformail@163.com
 * @create: 2021-09-12 05:32
 **/

@Service
public class DingTalkUtil {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkUtil.class);



    @Resource
    private CloseableHttpClient client;

    /**
     *
     * @param keyword
     * @param msg
     * @throws IOException
     */
    public void sendMsg(String keyword,String msg) throws IOException {
        this.sendMsg(keyword,null,msg);
    }

    /**
     *
     * @param keyword
     * @param time
     * @param msg
     * @throws IOException https://oapi.dingtalk.com/robot/send?access_token=22ecb19ece39382e0e180c82070736357ead2ceb596a1432e30cfb005a709554
     */
    public void sendMsg(String keyword, String time, String msg) throws IOException {


        HttpPost httppost = new HttpPost("https://oapi.dingtalk.com/robot/send?access_token=22ecb19ece39382e0e180c82070736357ead2ceb596a1432e30cfb005a709554");
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> text = new HashMap<String, Object>();

        json.put("msgtype", "text");
        text.put("content", keyword + "(" + time + ") ： " + msg);
        json.put("text", text);

        StringEntity se = new StringEntity(JSON.toJSONString(json), "utf-8");
        httppost.setEntity(se);

        HttpResponse response = client.execute(httppost);
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            String result= EntityUtils.toString(response.getEntity(), "utf-8");
            logger.info(result);
        }

    }
}
