package com.github.halfbull.weightlog.statistics;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

@SuppressWarnings({"CanBeFinal", "unused"})
public class GraphModelTest {

    @Mock
    WeightDao weightDao;

    @Mock
    FloatDateConverter floatDateConverter;

    @NonNull
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private GraphModel model;

    @Before
    public void setUp() throws Exception {
        model = new GraphModel(weightDao, floatDateConverter);
    }

    @Test
    public void getGraph() throws Exception {

        List<Weight> dbList = new LinkedList<>();

        List<GraphPoint> expectedGraphList = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            Weight w = new Weight();
            w.setDate(new GregorianCalendar(2017, 2, 1 + i).getTime());
            w.setValue(i);
            dbList.add(w);

            GraphPoint p = new GraphPoint((float) i, w.getValue());
            expectedGraphList.add(p);

            when(floatDateConverter.dateToFloatMonth(w.getDate())).thenReturn(p.getFloatMonth());
        }

        when(weightDao.getAll()).thenReturn(dbList);

        List<GraphPoint> actualGraphList = model.getGraph();

        assertEquals(expectedGraphList.size(), actualGraphList.size());
        for (int i = 0; i < expectedGraphList.size(); i++) {
            GraphPoint expected = expectedGraphList.get(i);
            GraphPoint actual = actualGraphList.get(i);
            assertEquals(expected.getFloatMonth(), actual.getFloatMonth(), 0);
            assertEquals(expected.getValue(), actual.getValue(), 0);
        }
    }
}
