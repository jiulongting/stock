package vip.linhs.stock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date subtraction(String dateStr,int dayNum){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date dt= null;
        try {
            dt = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
//        rightNow.add(Calendar.YEAR,-1);//日期减1年
//        rightNow.add(Calendar.MONTH,3);//日期加3个月
        rightNow.add(Calendar.DAY_OF_YEAR,dayNum);//日期加10天
        Date dt1=rightNow.getTime();

       // String reStr = sdf.format(dt1);
        System.out.println(dt1);
        return dt1;
    }

    public static Date subtraction(Date date,int dayNum){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
//        rightNow.add(Calendar.YEAR,-1);//日期减1年
//        rightNow.add(Calendar.MONTH,3);//日期加3个月
        rightNow.add(Calendar.DAY_OF_YEAR,dayNum);//日期加10天
        Date dt1=rightNow.getTime();

        // String reStr = sdf.format(dt1);
        System.out.println(dt1);
        return dt1;
    }
}
