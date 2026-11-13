/** Measures worst-case allocate()+release() for the scanning design vs the stack design. */
public class AllocationBenchmark {
    public static void main(String[] args) {
        int[][] shapes = {{1, 20}, {3, 50}, {5, 100}, {50, 1000}, {100, 2000}};
        System.out.println("Floors x Slots | scan (ns/op) | stack (ns/op)   [facility full except last slot]");
        for (int[] sh : shapes) {
            int F = sh[0], S = sh[1];
            int[] size = new int[F];
            java.util.Arrays.fill(size, S);
            ParkingFacility a = new ParkingFacility(size);
            FastFacility b = new FastFacility(size);
            for (int i = 0; i < F * S; i++) { a.allocate("V" + i); b.allocate("V" + i); }
            a.release(F - 1, S - 1); b.release(F - 1, S - 1);      // only the LAST slot is free
            int reps = 20000;
            for (int i = 0; i < 2000; i++) { a.allocate("X"); a.release(F - 1, S - 1); b.allocate("X"); b.release(F - 1, S - 1); }
            long t0 = System.nanoTime();
            for (int i = 0; i < reps; i++) { a.allocate("X"); a.release(F - 1, S - 1); }
            long scan = (System.nanoTime() - t0) / reps;
            t0 = System.nanoTime();
            for (int i = 0; i < reps; i++) { b.allocate("X"); b.release(F - 1, S - 1); }
            long stack = (System.nanoTime() - t0) / reps;
            System.out.printf("%4d x %-6d | %12d | %12d%n", F, S, scan, stack);
        }
    }
}
