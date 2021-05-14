package at.jit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class DmnFromCsvMain_CmdArgsParsingErrorsTest {
    static final String USAGE_MESSAGE = "usage: java -jar csv2dmn-converter.jar [-c] [-d] -i inputFile -o\n" +
            "            outputFile\n" +
            "Either -c or -d must be provided.\n" +
            " -c,--csv-to-dmn    Convert CSV to DMN\n" +
            " -d,--dmn-to-csv    Convert DMN to CSV\n" +
            " -i,--input-file    Input file (CSV or DMN)\n" +
            " -o,--output-file   Output file (CSV or DMN)";

    private final String[] args;
    private final String expectedErrorMessage;

    public DmnFromCsvMain_CmdArgsParsingErrorsTest(final String[] args, final String expectedErrorMessage) {
        this.args = args;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Parameterized.Parameters
    public static Collection testData() {
        return Arrays.asList(new Object[][] {
                {new String[0], "Invalid input: Missing required options: i, o"},
                {new String[]{"-i", "inputFile.csv", "-o", "outputFile.csv"},
                        "Mode (CSV to DMN, DMN to CSV) not specified"},
                {new String[] {"-o", "outputFile.csv"}, "Invalid input: Missing required option: i"},
                {new String[] {"-i", "inputFile.csv"}, "Invalid input: Missing required option: o"}
        });
    }

    @Test
    public void givenWrongCommandLineArguments_whenRun_thenPrintCorrectErrorMessage() throws UnsupportedEncodingException {
        // Given
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream sysErr = spy(new PrintStream(baos, true, StandardCharsets.UTF_8.name()));
        final DmnFromCsvMain app = spy(new DmnFromCsvMain(sysErr));

        // When
        app.run(new String[]{"-i", "inputFile.csv"});

        // Then
        final String actualOutput = extractAndNormalizeActualOutput(baos);
        final String expectedOutput = composeExpectedErrorOutput("Invalid input: Missing required option: o");
        assertEquals(expectedOutput, actualOutput);
        verify(sysErr).flush();
        verify(app, never()).convertCsvToDmn(anyString(), any());
        verify(app, never()).convertDmnToCsv(anyString(), anyString());

    }
    private String extractAndNormalizeActualOutput(final ByteArrayOutputStream baos) {
        return baos.toString().trim().replaceAll("\\r\\n?", "\n");
    }

    private String composeExpectedErrorOutput(final String messageAtTheTop) {
        return String.format("%s%s%s", messageAtTheTop, "\n", USAGE_MESSAGE);
    }
}
