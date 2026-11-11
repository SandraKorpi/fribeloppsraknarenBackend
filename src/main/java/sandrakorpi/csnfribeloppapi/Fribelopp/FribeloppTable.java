package sandrakorpi.csnfribeloppapi.Fribelopp;

import java.util.List;
import java.util.Map;

/**
 * Fribelopp per kalenderhalvår för studiemedel.
 *
 * CSN anger fribeloppet som en tabell: antal veckor med studiemedel i halvåret (1-26)
 * och studietakt (100, 75 eller 50 procent). Ett lägre antal veckor eller en lägre
 * studietakt ger ett högre fribelopp. Tabellen skalas med prisbasbeloppet, så beloppen
 * för andra år räknas fram från 2026 års tabell. Det exakta fribeloppet för ett visst
 * år står alltid i CSN:s beslut, och kan anges som eget värde i appen.
 *
 * Klassen har inga beroenden till Spring, så att den går att testa fristående.
 */
public final class FribeloppTable {

    public static final int MIN_WEEKS = 1;
    public static final int MAX_WEEKS = 26;
    public static final List<Integer> STUDY_RATES = List.of(100, 75, 50);

    /** Prisbasbelopp per år. Lägg till nästa år när regeringen har fastställt det. */
    private static final Map<Integer, Integer> PRICE_BASE_AMOUNT = Map.of(
            2025, 58_800,
            2026, 59_200,
            2027, 59_600
    );

    /** År vars tabell är hämtad direkt från CSN. Övriga år räknas fram från prisbasbeloppet. */
    static final int OFFICIAL_YEAR = 2026;

    /**
     * CSN:s tabell för 2026. Rad = antal veckor (1-26), kolumner = studietakt 100, 75, 50.
     * Källa: https://www.csn.se/fribelopp
     */
    private static final int[][] TABLE_2026 = {
            {223_669, 225_007, 226_457},
            {217_932, 220_709, 223_592},
            {212_196, 216_411, 220_727},
            {206_460, 212_113, 217_861},
            {200_723, 207_815, 214_996},
            {194_987, 203_517, 212_131},
            {189_250, 199_219, 209_266},
            {183_514, 194_921, 206_400},
            {177_777, 190_624, 203_535},
            {172_041, 186_326, 200_670},
            {166_304, 182_028, 197_804},
            {160_568, 177_730, 194_939},
            {154_831, 173_432, 192_074},
            {149_095, 169_134, 189_209},
            {143_358, 164_836, 186_343},
            {137_622, 160_538, 183_478},
            {131_885, 156_240, 180_613},
            {126_149, 151_942, 177_748},
            {120_412, 147_644, 174_882},
            {114_676, 143_346, 172_017},
            {108_939, 139_048, 169_152},
            {103_203, 134_751, 166_286},
            {97_466, 130_453, 163_421},
            {91_730, 126_155, 160_556},
            {85_993, 121_857, 157_691},
            {80_257, 117_559, 154_825}
    };

    private FribeloppTable() {
    }

    public static boolean isSupportedYear(int year) {
        return PRICE_BASE_AMOUNT.containsKey(year);
    }

    /** True om beloppet är beräknat från prisbasbeloppet och inte hämtat direkt ur CSN:s tabell. */
    public static boolean isEstimated(int year) {
        return year != OFFICIAL_YEAR;
    }

    /**
     * @param year       kalenderår
     * @param weeks      antal veckor med studiemedel i halvåret, 1-26
     * @param studyRate  studietakt i procent: 100, 75 eller 50
     * @return fribelopp i kronor för kalenderhalvåret
     */
    public static long fribelopp(int year, int weeks, int studyRate) {
        if (weeks < MIN_WEEKS || weeks > MAX_WEEKS) {
            throw new IllegalArgumentException("Antal veckor måste vara mellan " + MIN_WEEKS + " och " + MAX_WEEKS);
        }
        int column = STUDY_RATES.indexOf(studyRate);
        if (column < 0) {
            throw new IllegalArgumentException("Studietakten måste vara 100, 75 eller 50 procent");
        }
        Integer priceBase = PRICE_BASE_AMOUNT.get(year);
        if (priceBase == null) {
            throw new IllegalArgumentException("Fribeloppet för " + year + " är inte inlagt i appen ännu");
        }

        int base = TABLE_2026[weeks - 1][column];
        if (year == OFFICIAL_YEAR) {
            return base;
        }
        return Math.round(base * (double) priceBase / PRICE_BASE_AMOUNT.get(OFFICIAL_YEAR));
    }
}
