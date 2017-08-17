package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.Writer;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"CanBeFinal", "unused"})
public class CsvSerializerTest {

    @Mock
    Writer writer;

    @NonNull
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CsvSerializer serializer;

    @Before
    public void setUp() {
        serializer = new CsvSerializer(writer);
    }

    @Test
    public void serialize_list() throws Exception {

        List<Weight> weights = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            Weight weight = new Weight();
            weight.setDate(new GregorianCalendar(2017, 1, 3 + i).getTime());
            weight.setValue(12.5f + i);
            weights.add(weight);
        }

        serializer.serialize(weights);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(writer, times(3)).write(captor.capture());

        List<String> expected = new LinkedList<>();
        expected.add("2017-02-03T00:00:00\t12.5\r\n");
        expected.add("2017-02-04T00:00:00\t13.5\r\n");
        expected.add("2017-02-05T00:00:00\t14.5\r\n");

        assertArrayEquals(expected.toArray(), captor.getAllValues().toArray());
    }
}
