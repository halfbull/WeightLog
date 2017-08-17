package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CsvDeserializerTest {

    @Test
    public void deserialize_list_of_values() throws Exception {
        String original = "2017-02-03T00:00:00\t12.5\r\n"
                + "2017-02-04T00:00:00\t13.5\r\n"
                + "2017-02-05T00:00:00\t14.5\r\n";

        List<Weight> expected = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            Weight weight = new Weight();
            weight.setDate(new GregorianCalendar(2017, 1, 3 + i).getTime());
            weight.setValue(12.5f + i);
            expected.add(weight);
        }

        List<Weight> actual = deserialize(original);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(0).getDate(), actual.get(0).getDate());
            assertEquals(expected.get(0).getValue(), actual.get(0).getValue(), 0);
        }
    }

    @Test
    public void wrong_values_ignored() throws Exception {
        List<Weight> actual = deserialize("incorrect_value\r\n");
        assertEquals(actual.size(), 0);
    }

    @NonNull
    private List<Weight> deserialize(@NonNull String input) throws Exception {

        Reader r = new StringReader(input);
        CsvDeserializer deserializer = new CsvDeserializer(r);
        return deserializer.deserialize();
    }
}
