package at.jit;

import org.camunda.bpm.model.dmn.HitPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CsvPojoTest {
    private final String inputHitPolicy;
    private final HitPolicy expectedHitPolicy;

    public CsvPojoTest(final String inputHitPolicy,
                       final HitPolicy expectedHitPolicy) {
        this.inputHitPolicy = inputHitPolicy;
        this.expectedHitPolicy = expectedHitPolicy;
    }

    @Parameterized.Parameters
    public static Collection testData() {
        return Arrays.asList(new Object[][] {
                {"unique", HitPolicy.UNIQUE},
                {"any", HitPolicy.ANY},
                {"collect", HitPolicy.COLLECT},
                {"first", HitPolicy.FIRST},
                {"priority", HitPolicy.PRIORITY},
                {"rule order", HitPolicy.RULE_ORDER},
                {"output order", HitPolicy.OUTPUT_ORDER},
                {"", HitPolicy.UNIQUE},
        });
    }

    @Test
    public void test() {
        // Given
        final CsvPojo sut = new CsvPojo();

        // When
        sut.setHitPolicy(inputHitPolicy);

        // Then
        assertEquals(expectedHitPolicy, sut.getHitPolicy());
    }
}
