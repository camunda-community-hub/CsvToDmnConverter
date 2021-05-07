package at.jit;

import org.junit.Test;

public class DmnFromCsvMainTest {

    @Test
    public void givenCsvFile_whenRun_thenReturnCorrectResult() {
        // Given
        final DmnFromCsvMain sut = new DmnFromCsvMain();

        // When
        sut.run(new String[] {
                "1",
                "src/test/resources/t1.input.csv",
                "src/test/resources/t2.actout.dmn"
        });

        // Then
    }
}