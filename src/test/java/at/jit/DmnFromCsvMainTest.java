package at.jit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class DmnFromCsvMainTest {
    static final String INPUT_FILE = "src/test/resources/t1.input.csv";
    static final String ACT_RESULT_FILE = "src/test/resources/t2.actout.dmn";
    static final String EXP_RESULT_FILE = "src/test/resources/t2.expout.dmn";

    @Test
    public void givenCsvFile_whenRun_thenReturnCorrectResult() throws IOException {
        // Given
        final DmnFromCsvMain sut = new DmnFromCsvMain();

        // When
        sut.run(new String[] {
                "1",
                INPUT_FILE,
                ACT_RESULT_FILE
        });

        // Then
        assertTrue(FileUtils.contentEquals(new File(EXP_RESULT_FILE), new File(ACT_RESULT_FILE)));
    }
}