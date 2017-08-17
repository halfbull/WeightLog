package com.github.halfbull.weightlog.statistics;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class FloatDateConverter {

    float dateToFloatMonth(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);

        int years = c.get(Calendar.YEAR);
        float result = years * 12;

        int months = c.get(Calendar.MONTH);
        result += months;

        int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int days = c.get(Calendar.DAY_OF_MONTH) - 1;
        result += (float) days / daysInMonth;

        return result;
    }

    Date floatMonthToDate(float floatMonth) {
        int totalMonths = (int) Math.floor(floatMonth);

        int year = totalMonths / 12;
        int month = totalMonths % 12;

        Calendar c = new GregorianCalendar(year, month, 1);
        return c.getTime();
    }
}
