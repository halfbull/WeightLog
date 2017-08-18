package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"CanBeFinal", "unused"})
public class CsvImportModelTest {

    @Mock
    WeightDao weightDao;

    @Mock
    StreamFactory streamFactory;

    @Mock
    CsvConverterFactory csvConverterFactory;

    @Mock
    CsvDeserializer csvDeserializer;

    @NonNull
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CsvImportModel model;

    @Captor
    ArgumentCaptor<List<Weight>> weightListCaptor;

    @Before
    public void setUp() throws FileNotFoundException {
        model = new CsvImportModel(weightDao, streamFactory, csvConverterFactory);
        when(streamFactory.getReader(any(File.class))).thenReturn(mock(FileReader.class));
        when(csvConverterFactory.getDeserializer(any(Reader.class))).thenReturn(csvDeserializer);
    }

    @Test
    public void import_to_empty_database() throws Exception {

        List<Weight> csvList = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            Weight w = new Weight();
            w.setDate(new GregorianCalendar(2017, 0, 1 + i).getTime());
            w.setValue(i);

            csvList.add(w);
        }

        List<Weight> databaseList = new LinkedList<>();

        when(csvDeserializer.deserialize()).thenReturn(csvList);
        when(weightDao.getAll()).thenReturn(databaseList);

        int processedRecords = model.importLog(new File("dummy.csv"));

        verify(weightDao, times(1)).insertList(weightListCaptor.capture());

        Assert.assertArrayEquals(csvList.toArray(), weightListCaptor.getValue().toArray());

        Assert.assertEquals(3, processedRecords);
    }

    @Test
    public void ignore_duplicates_in_csv_list() throws Exception {

        List<Weight> csvList = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            Weight w = new Weight();
            w.setDate(new GregorianCalendar(2017, 0, 1).getTime());
            w.setValue(i);

            csvList.add(w);
        }

        List<Weight> databaseList = new LinkedList<>();

        when(csvDeserializer.deserialize()).thenReturn(csvList);
        when(weightDao.getAll()).thenReturn(databaseList);

        int processedRecords = model.importLog(new File("dummy.csv"));

        verify(weightDao, times(1)).insertList(weightListCaptor.capture());
        assertEquals(csvList.get(0), weightListCaptor.getValue().get(0));

        Assert.assertEquals(1, processedRecords);
    }

    @Test
    public void ignore_duplicates_in_db_list() throws Exception {

        List<Weight> csvList = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            Weight w = new Weight();
            w.setDate(new GregorianCalendar(2017, 0, 1).getTime());
            w.setValue(i);

            csvList.add(w);
        }

        List<Weight> databaseList = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            Weight w = new Weight();
            w.setDate(new GregorianCalendar(2017, 0, 1).getTime());
            w.setValue(i);

            databaseList.add(w);
        }

        when(csvDeserializer.deserialize()).thenReturn(csvList);
        when(weightDao.getAll()).thenReturn(databaseList);

        int processedRecords = model.importLog(new File("dummy.csv"));

        ArgumentCaptor<Weight> captor = ArgumentCaptor.forClass(Weight.class);
        verify(weightDao, times(0)).insert(captor.capture());

        Assert.assertEquals(0, processedRecords);
    }

    @Test
    public void treat_dates_within_same_second_as_same() throws Exception {
        List<Weight> csvList = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            Weight w = new Weight();
            w.setDate(new GregorianCalendar(2017, 0, 1 + i, 2, 1, 1).getTime());
            w.setValue(i);

            csvList.add(w);
        }

        List<Weight> databaseList = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            GregorianCalendar c = new GregorianCalendar(2017, 0, 1 + i, 2, 1, 1);
            c.add(Calendar.MILLISECOND, i);

            Weight w = new Weight();
            w.setDate(c.getTime());
            w.setValue(i);

            databaseList.add(w);
        }

        when(csvDeserializer.deserialize()).thenReturn(csvList);
        when(weightDao.getAll()).thenReturn(databaseList);

        int processedRecords = model.importLog(new File("dummy.csv"));

        ArgumentCaptor<Weight> captor = ArgumentCaptor.forClass(Weight.class);
        verify(weightDao, times(0)).insert(captor.capture());

        Assert.assertEquals(0, processedRecords);
    }
}
