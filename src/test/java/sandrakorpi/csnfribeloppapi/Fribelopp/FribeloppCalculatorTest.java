package sandrakorpi.csnfribeloppapi.Fribelopp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FribeloppCalculatorTest {

    @Test
    void underTheLimit() {
        FribeloppCalculator.Result result = FribeloppCalculator.evaluate(114_676, 100_000);

        assertEquals(14_676, result.remaining());
        assertEquals(0, result.over());
        assertEquals(0, result.estimatedReduction());
        assertFalse(result.exceeded());
    }

    @Test
    void overTheLimitReducesStudyAidBy61PercentOfTheExcess() {
        // 4 549 kr över gränsen ger cirka 2 775 kr lägre studiemedel.
        FribeloppCalculator.Result result = FribeloppCalculator.evaluate(115_451, 120_000);

        assertTrue(result.exceeded());
        assertEquals(4_549, result.over());
        assertEquals(2_775, result.estimatedReduction());
        assertEquals(0, result.remaining());
    }

    @Test
    void exactlyOnTheLimitIsNotExceeded() {
        FribeloppCalculator.Result result = FribeloppCalculator.evaluate(114_676, 114_676);

        assertFalse(result.exceeded());
        assertEquals(0, result.remaining());
    }

    @Test
    void extraHoursUseAverageHourlyPay() {
        assertEquals(97.8, FribeloppCalculator.extraHours(14_676, 150.0), 0.001);
        assertEquals(0.0, FribeloppCalculator.extraHours(10_000, 0.0), 0.001);
        assertEquals(0.0, FribeloppCalculator.extraHours(0, 150.0), 0.001);
    }

    @Test
    void rejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> FribeloppCalculator.evaluate(100_000, -1));
        assertThrows(IllegalArgumentException.class, () -> FribeloppCalculator.evaluate(-1, 0));
    }
}
