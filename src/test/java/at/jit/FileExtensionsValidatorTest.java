package at.jit;

import at.jit.helper.FileExtensionsValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static at.jit.constants.Modes.CSV_TO_DMN;
import static at.jit.constants.Modes.DMN_TO_CSV;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FileExtensionsValidatorTest {
    private final String mode;
    private final String inputFileName;
    private final String outputFileName;
    private final Boolean expectedResult;

    public FileExtensionsValidatorTest(final String mode,
                                       final String inputFileName,
                                       final String outputFileName,
                                       final Boolean expectedResult) {
        this.mode = mode;
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection testData() {
        return Arrays.asList(new Object[][] {
                {CSV_TO_DMN, "someFile.csv", "someFile.dmn", true},
                {CSV_TO_DMN, "someFile.CSV", "someFile.DMN", true},
                {CSV_TO_DMN, "someFile.CSV", null, false},
                {CSV_TO_DMN, null, "someFile.DMN", false},
                {DMN_TO_CSV, "someFile.dmn", "someFile.csv", true},
                {DMN_TO_CSV, "someFile.DMN", "someFile.CSV", true},
                {DMN_TO_CSV, "someFile.DMN", null, false},
                {DMN_TO_CSV, null, "someFile.CSV", false},
                {null, "someFile.DMN", "someFile.CSV", false},
                {"abrakadabra", "someFile.DMN", "someFile.CSV", false},
        });
    }

    @Test
    public void test() {
        // Given

        // When
        final FileExtensionsValidator sut = new FileExtensionsValidator();
        final boolean actRes = sut.fileExtensionsValid(inputFileName, outputFileName, mode);

        // Then
        assertEquals(expectedResult, actRes);
    }
}