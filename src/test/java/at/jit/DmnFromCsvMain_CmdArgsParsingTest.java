package at.jit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class DmnFromCsvMain_CmdArgsParsingTest {
    private final String[] args;
    private final boolean csvToDmnConversionExpected;
    private final boolean dmnToCsvConversionExecuted;
    private final String expectedInputFileName;
    private final String expectedOutputFileName;

    public DmnFromCsvMain_CmdArgsParsingTest(String[] args, boolean csvToDmnConversionExpected, boolean dmnToCsvConversionExecuted, String expectedInputFileName, String expectedOutputFileName) {
        this.args = args;
        this.csvToDmnConversionExpected = csvToDmnConversionExpected;
        this.dmnToCsvConversionExecuted = dmnToCsvConversionExecuted;
        this.expectedInputFileName = expectedInputFileName;
        this.expectedOutputFileName = expectedOutputFileName;
    }

    @Parameterized.Parameters
    public static Collection testData() {
        return Arrays.asList(new Object[][]{
                {new String[]{"-c", "-i", "inputFile.csv", "-o", "outputFile.dmn"}, true, false, "inputFile.csv", "outputFile.dmn"},
                {new String[]{"--csv-to-dmn", "-i", "inputFile.csv", "-o", "outputFile.dmn"}, true, false, "inputFile.csv", "outputFile.dmn"},
                {new String[]{"-c", "-i", "inputFile.CSV", "-o", "outputFile.DMN"}, true, false, "inputFile.CSV", "outputFile.DMN"},
                {new String[]{"-d", "-i", "inputFile.dmn", "-o", "outputFile.csv"}, false, true, "inputFile.dmn", "outputFile.csv"},
                {new String[]{"-d", "-i", "inputFile.DMN", "-o", "outputFile.CSV"}, false, true, "inputFile.DMN", "outputFile.CSV"},
                {new String[]{"--dmn-to-csv", "--input-file", "inputFile.dmn", "--output-file", "outputFile.csv"}, false, true, "inputFile.dmn", "outputFile.csv"},
        });
    }

    @Test
    public void givenCorrectInputData_whenRun_thenRunCorrectConversion() throws UnsupportedEncodingException {
        // Given
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream sysErr = spy(new PrintStream(baos, true, StandardCharsets.UTF_8.name()));
        final DmnFromCsvMain app = spy(new DmnFromCsvMain(sysErr));

        doNothing().when(app).convertDmnToCsv(anyString(), anyString());
        doNothing().when(app).convertCsvToDmn(anyString(), anyString());

        // When
        app.run(args);

        // Then
        final String actualOutput = Utils.extractAndNormalizeActualOutput(baos);
        assertEquals("", actualOutput);
        if (csvToDmnConversionExpected) {
            verify(app).convertCsvToDmn(expectedInputFileName, expectedOutputFileName);
        } else {
            verify(app, never()).convertCsvToDmn(anyString(), anyString());
        }

        if (dmnToCsvConversionExecuted) {
            verify(app).convertDmnToCsv(expectedInputFileName, expectedOutputFileName);
        } else {
            verify(app, never()).convertDmnToCsv(anyString(), anyString());
        }
        Mockito.verifyNoInteractions(sysErr);
    }
}
