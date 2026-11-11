package sandrakorpi.csnfribeloppapi.Fribelopp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FribeloppTableTest {

    // Värden hämtade ur CSN:s fribeloppstabell för 2026: https://www.csn.se/fribelopp
    @ParameterizedTest
    @CsvSource({
            "1,100,223669", "1,75,225007", "1,50,226457",
            "10,100,172041", "10,75,186326", "10,50,200670",
            "20,100,114676", "20,75,143346", "20,50,172017",
            "26,100,80257", "26,75,117559", "26,50,154825"
    })
    void matchesCsnTableFor2026(int weeks, int studyRate, long expected) {
        assertEquals(expected, FribeloppTable.fribelopp(2026, weeks, studyRate));
    }

    @Test
    void scalesWithPriceBaseAmountForOtherYears() {
        // CSN har publicerat 115 451 kr för 20 veckors heltidsstudier 2027.
        assertEquals(115_451, FribeloppTable.fribelopp(2027, 20, 100));
        // CSN:s egen 2025-tabell: 113 901 kr för 20 veckor heltid, 142 378 kr för 75 %.
        assertEquals(113_901, FribeloppTable.fribelopp(2025, 20, 100), 1);
        assertEquals(142_378, FribeloppTable.fribelopp(2025, 20, 75), 1);
    }

    @Test
    void onlyTheCsnTableYearIsMarkedOfficial() {
        assertFalse(FribeloppTable.isEstimated(2026));
        assertTrue(FribeloppTable.isEstimated(2027));
    }

    @Test
    void fewerWeeksOrLowerStudyRateGivesHigherFribelopp() {
        for (int weeks = 2; weeks <= FribeloppTable.MAX_WEEKS; weeks++) {
            assertTrue(FribeloppTable.fribelopp(2026, weeks, 100) < FribeloppTable.fribelopp(2026, weeks - 1, 100));
        }
        for (int weeks = 1; weeks <= FribeloppTable.MAX_WEEKS; weeks++) {
            assertTrue(FribeloppTable.fribelopp(2026, weeks, 100) < FribeloppTable.fribelopp(2026, weeks, 75));
            assertTrue(FribeloppTable.fribelopp(2026, weeks, 75) < FribeloppTable.fribelopp(2026, weeks, 50));
        }
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> FribeloppTable.fribelopp(2026, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> FribeloppTable.fribelopp(2026, 27, 100));
        assertThrows(IllegalArgumentException.class, () -> FribeloppTable.fribelopp(2026, 10, 60));
        assertThrows(IllegalArgumentException.class, () -> FribeloppTable.fribelopp(2031, 10, 100));
    }
}
