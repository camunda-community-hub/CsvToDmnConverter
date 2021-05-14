package at.jit;

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class DmnFromCsvMain_CmdArgsParsingTest {

    public static final String USAGE_MESSAGE = "usage: java -jar csv2dmn-converter.jar [-c] [-d] -i inputFile -o\n" +
            "            outputFile\n" +
            "Either -c or -d must be provided.\n" +
            " -c,--csv-to-dmn    Convert CSV to DMN\n" +
            " -d,--dmn-to-csv    Convert DMN to CSV\n" +
            " -i,--input-file    Input file (CSV or DMN)\n" +
            " -o,--output-file   Output file (CSV or DMN)";

    @Test
    public void givenNoCommandLineArguments_thenRun_thenPrintCorrectErrorMessage() throws IOException {
        // Given
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream sysErr = spy(new PrintStream(baos, true, StandardCharsets.UTF_8.name()));
        //final PrintStream sysErr = mock(PrintStream.class);
        final DmnFromCsvMain app = spy(new DmnFromCsvMain(sysErr));

        // When
        app.run(new String[0]);

        // Then
        final String actualOutput = baos.toString().trim().replaceAll("\\r\\n?", "\n");
        final String expectedOutput = String.format("%s%s%s", "Invalid input: Missing required options: i, o", "\n", USAGE_MESSAGE);
        assertEquals(expectedOutput, actualOutput);
        verify(sysErr).flush();
    }
}
