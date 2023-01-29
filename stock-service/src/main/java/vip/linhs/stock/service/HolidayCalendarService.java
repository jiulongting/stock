package vip.linhs.stock.service;

import java.util.Date;

public interface HolidayCalendarService {

    void updateCurrentYear();

    boolean isBusinessDate(Date date);

    boolean isBusinessTime(Date date);

    Date businesDateSubtraction(String createTimeStr, int num);
}
