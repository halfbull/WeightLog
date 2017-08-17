package com.github.halfbull.weightlog.importexport;

import android.support.annotation.NonNull;

import com.github.halfbull.weightlog.database.Weight;
import com.github.halfbull.weightlog.database.WeightDao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
public class CsvExportModelTest {

    @Mock
    WeightDao weightDao;

    @Mock
    StreamFactory streamFactory;

    @Mock
    CsvConverterFactory csvConverterFactory;

    @Mock
    CsvSerializer csvSerializer;

    @NonNull
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Captor
    ArgumentCaptor<List<Weight>> weightListCaptor;

    private CsvExportModel model;

    @Before
    public void setUp() throws Exception {
        model = new CsvExportModel(weightDao, streamFactory, csvConverterFactory);
        when(streamFactory.getWriter(any(File.class))).thenReturn(mock(FileWriter.class));
        when(csvConverterFactory.getSerializer(any(Writer.class))).thenReturn(csvSerializer);
    }

    @Test
    public void write_entries_from_database() throws IOException {

        List<Weight> databaseList = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            Weight w = new Weight();
            w.setDate(new GregorianCalendar(2017, 0, 1 + i).getTime());
            w.setValue(i);

            databaseList.add(w);
        }

        when(weightDao.getAll()).thenReturn(databaseList);

        int processedRecords = model.exportLog(new File("dummy.csv"));

        assertEquals(3, processedRecords);

        verify(csvSerializer, times(1)).serialize(weightListCaptor.capture());

        assertArrayEquals(databaseList.toArray(), weightListCaptor.getValue().toArray());
    }
}
