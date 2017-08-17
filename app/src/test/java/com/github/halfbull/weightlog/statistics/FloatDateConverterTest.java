package com.github.halfbull.weightlog.statistics;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
@SuppressWarnings({"CanBeFinal", "unused"})
public class FloatDateConverterTest {

    @NonNull
    FloatDateConverter converter = new FloatDateConverter();

    @NonNull
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Parameterized.Parameter()
    public String dateStr;

    @Parameterized.Parameter(1)
    public float floatMonth;

    @Parameterized.Parameters(name = "\"{0}\" = {1}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {"2017-01-01", 2017 * 12},
                {"2017-01-02", 2017 * 12 + 01f / 31},
                {"2017-01-31", 2017 * 12 + 30f / 31},
                {"2017-02-01", 2017 * 12 + 1 + 0f / 28},
                {"2017-02-02", 2017 * 12 + 1 + 1f / 28}
        };
        return Arrays.asList(data);
    }

    @Test
    public void date_to_floatMonth() throws ParseException {

        Date date = dateFormat.parse(dateStr);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        float actual = converter.dateToFloatMonth(date);
        assertEquals(floatMonth, actual, 0);
    }

    @Test
    public void floatMonth_to_date() throws ParseException {

        Date actualDate = converter.floatMonthToDate(floatMonth);

        String actual = dateFormat.format(actualDate);

        assertEquals(cleanDays(dateStr), actual);
    }

    private String cleanDays(String str) throws ParseException {

        Date date = dateFormat.parse(str);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        date = calendar.getTime();
        return dateFormat.format(date);
    }
}
