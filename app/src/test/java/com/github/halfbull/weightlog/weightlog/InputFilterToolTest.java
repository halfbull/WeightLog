package com.github.halfbull.weightlog.weightlog;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
@SuppressWarnings({"CanBeFinal", "WeakerAccess", "unused"})
public class InputFilterToolTest {

    @NonNull
    @Parameterized.Parameters(name = "{index} : ({0}) s:{1} e:{2} ({3}) s:{4} e:{5} => future:{6} isDelete:{7} deletedPart:{8}")
    public static Object[] inputs() {
        return new Object[]{
                new Object[]{"1", 0, 1, "", 0, 0, "1", false, ""},
                new Object[]{"2", 0, 1, "1", 1, 1, "12", false, ""},
                new Object[]{".", 0, 1, "12", 2, 2, "12.", false, ""},
                new Object[]{"0", 0, 1, "12.", 3, 3, "12.0", false, ""},
                new Object[]{"8", 0, 1, "12.0", 4, 4, "12.08", false, ""},
                new Object[]{"0", 0, 1, "11", 0, 0, "011", false, ""},
                new Object[]{"0", 0, 1, "11", 1, 1, "101", false, ""},
                new Object[]{"000", 0, 3, "111", 0, 0, "000111", false, ""},
                new Object[]{"000", 0, 3, "111", 0, 3, "000", false, ""},
                new Object[]{"000", 0, 3, "111", 1, 1, "100011", false, ""},
                new Object[]{"", 0, 0, "123", 2, 3, "12", true, "3"},
                new Object[]{"", 0, 0, "123", 1, 2, "13", true, "2"},
                new Object[]{"", 0, 0, "123", 0, 1, "23", true, "1"},
        };
    }

    @Parameter()
    public CharSequence source;

    @Parameter(1)
    public int start;

    @Parameter(2)
    public int end;

    @Parameter(3)
    public CharSequence destination;

    @Parameter(4)
    public int dStart;

    @Parameter(5)
    public int dEnd;

    @Parameter(6)
    public String futureString;

    @Parameter(7)
    public boolean isDeleteAction;

    @Parameter(8)
    public String deletedPart;

    private InputFilterTool inputFilterTool;

    @Before
    public void setUp() {
        inputFilterTool = new InputFilterTool();
    }

    @Test
    public void getDesiredString() throws Exception {

        CharSequence actual = inputFilterTool.getFutureString(source, start, end, destination, dStart, dEnd);
        assertEquals(futureString, actual);
    }

    @Test
    public void isDeleteAction() {
        boolean actual = inputFilterTool.isDeleteAction(destination, futureString);
        assertEquals(isDeleteAction, actual);
    }

    @Test
    public void getDeletedPart() {
        if (isDeleteAction) {
            CharSequence actual = inputFilterTool.getDeletedPart(destination, dStart, dEnd);
            assertEquals(deletedPart, actual);
        } else {
            assertTrue(true);
        }
    }
}
