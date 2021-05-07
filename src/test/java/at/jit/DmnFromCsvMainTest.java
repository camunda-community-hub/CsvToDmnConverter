package at.jit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class DmnFromCsvMainTest {
    static final String INPUT_CSV_FILE = "src/test/resources/t1.input.csv";
    static final String ACT_DMN_RESULT_FILE = "src/test/resources/t1.actout.dmn";
    static final String EXP_RESULT_FILE = "src/test/resources/t1.expout.dmn";

    static final String INPUT_DMN_FILE = "src/test/resources/t2.input.dmn";
    static final String ACT_CSV_RESULT_FILE = "src/test/resources/t2.actout.csv";
    static final String EXP_CSV_RESULT_FILE = "src/test/resources/t2.expout.csv";

    @Test
    public void givenCsvFile_whenRun_thenReturnCorrectDmnFile() throws IOException {
        // Given
        final DmnFromCsvMain sut = new DmnFromCsvMain();

        // When
        sut.run(new String[] {
                Modes.CSV_TO_DMN,
                INPUT_CSV_FILE,
                ACT_DMN_RESULT_FILE
        });

        // Then
        assertTrue(FileUtils.contentEquals(new File(EXP_RESULT_FILE), new File(ACT_DMN_RESULT_FILE)));
    }

    @Test
    public void givenDmnFile_whenRun_thenReturnCorrectCsvFile() throws IOException {
        // Given
        final DmnFromCsvMain sut = new DmnFromCsvMain();

        // When
        sut.run(new String[] {
                Modes.DMN_TO_CSV,
                INPUT_DMN_FILE,
                ACT_CSV_RESULT_FILE
        });

        // Then
        assertTrue(FileUtils.contentEqualsIgnoreEOL(new File(EXP_CSV_RESULT_FILE), new File(ACT_CSV_RESULT_FILE), "UTF-8"));
    }
}