package sandrakorpi.csnfribeloppapi.Fribelopp;

/**
 * Jämför inkomst med fribelopp. Inga beroenden till Spring.
 */
public final class FribeloppCalculator {

    /** Studiemedlet minskas med 61 procent av den inkomst som överstiger fribeloppet. */
    public static final double REDUCTION_FACTOR = 0.61;

    private FribeloppCalculator() {
    }

    public record Result(
            long fribelopp,
            long income,
            long remaining,
            long over,
            long estimatedReduction,
            boolean exceeded
    ) {
    }

    public static Result evaluate(double fribelopp, double income) {
        if (fribelopp < 0 || income < 0) {
            throw new IllegalArgumentException("Fribelopp och inkomst kan inte vara negativa");
        }
        long limit = Math.round(fribelopp);
        long earned = Math.round(income);
        long difference = limit - earned;

        long remaining = Math.max(difference, 0);
        long over = Math.max(-difference, 0);
        long reduction = Math.round(over * REDUCTION_FACTOR);

        return new Result(limit, earned, remaining, over, reduction, over > 0);
    }

    /**
     * Ungefär hur många timmar som får arbetas till innan fribeloppet nås.
     *
     * @param remaining       återstående kronor till fribeloppet
     * @param averageHourlyPay medeltimlön inklusive semesterersättning
     */
    public static double extraHours(long remaining, double averageHourlyPay) {
        if (remaining <= 0 || averageHourlyPay <= 0) {
            return 0.0;
        }
        return Math.round(remaining / averageHourlyPay * 10.0) / 10.0;
    }
}
