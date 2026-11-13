import java.util.Arrays;

/** Task 2 - Data types, jagged array, and the integer-division / cast risk in fee calculation. */
public class DataTypeDemo {
    static final double RATE_CAR = 30.0;

    // WRONG 1: integer division truncates -> 130/60 = 2 hours
    static double wrongIntDivision(int minutes) { return (minutes / 60) * RATE_CAR; }

    // WRONG 2: casting AFTER the division is too late
    static double wrongLateCast(int minutes) { return (double) (minutes / 60) * RATE_CAR; }

    // WRONG 3: exact fractional hours, but the policy is "every started hour is charged in full"
    static double wrongFractional(int minutes) { return ((double) minutes / 60) * RATE_CAR; }

    // CORRECT: ceiling division using integers only
    static double correct(int minutes) { return ((minutes + 59) / 60) * RATE_CAR; }

    public static void main(String[] args) {
        // Input: command-line args if given, otherwise defaults (so it runs without typing)
        int minutes = args.length > 0 ? Integer.parseInt(args[0]) : 130;

        System.out.println("Parked for " + minutes + " minutes, car rate Rs " + RATE_CAR + "/hour");
        System.out.println("Wrong (int division)    : Rs " + wrongIntDivision(minutes));
        System.out.println("Wrong (late cast)       : Rs " + wrongLateCast(minutes));
        System.out.println("Wrong (fractional hours): Rs " + wrongFractional(minutes));
        System.out.println("Correct (ceiling)       : Rs " + correct(minutes));

        // long -> int narrowing risk (Duration.toMinutes() returns long)
        long hugeMinutes = 3_000_000_000L;
        int narrowed = (int) hugeMinutes;
        System.out.println("\n(int) 3000000000L = " + narrowed + "  <- silent overflow");
        try { Math.toIntExact(hugeMinutes); }
        catch (ArithmeticException e) { System.out.println("Math.toIntExact detects it: " + e.getMessage()); }

        // Jagged array: floor 0 has 3 slots, floor 1 has 5, floor 2 has 2
        String[][] slots = new String[3][];
        int[] size = {3, 5, 2};
        for (int f = 0; f < slots.length; f++) slots[f] = new String[size[f]];
        slots[1][4] = "TN01AB1234";
        System.out.println("\nJagged array lengths per floor: ");
        for (int f = 0; f < slots.length; f++)
            System.out.println("  floor " + f + " -> " + slots[f].length + " slots  " + Arrays.toString(slots[f]));
    }
}
