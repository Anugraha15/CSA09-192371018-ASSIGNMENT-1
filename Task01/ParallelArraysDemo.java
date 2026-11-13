/** Task 1 - Object design vs parallel-array design: search by registration number + new attribute. */
public class ParallelArraysDemo {

    // ---- Design A: each vehicle is an object ----
    static class VehicleRecord {
        final String regNo; final String type; final long entryMinute;
        VehicleRecord(String regNo, String type, long entryMinute) {
            this.regNo = regNo; this.type = type; this.entryMinute = entryMinute;
        }
    }

    static VehicleRecord findObject(VehicleRecord[] list, String regNo) {
        for (VehicleRecord v : list) if (v.regNo.equals(regNo)) return v;
        return null;
    }

    // ---- Design B: parallel arrays (index i describes one vehicle) ----
    static int findParallel(String[] regNo, String regToFind) {
        for (int i = 0; i < regNo.length; i++) if (regNo[i].equals(regToFind)) return i;
        return -1;
    }

    public static void main(String[] args) {
        int n = 200_000;
        VehicleRecord[] objs = new VehicleRecord[n];
        String[] regNo = new String[n];
        String[] type = new String[n];
        long[] entry = new long[n];
        for (int i = 0; i < n; i++) {
            String r = "TN" + (i % 99) + "AB" + i;
            objs[i] = new VehicleRecord(r, "Car", i);
            regNo[i] = r; type[i] = "Car"; entry[i] = i;
        }
        String target = "TN" + ((n - 1) % 99) + "AB" + (n - 1);   // worst case: last element

        // warm-up
        for (int i = 0; i < 50; i++) { findObject(objs, target); findParallel(regNo, target); }

        int reps = 300;
        long t0 = System.nanoTime();
        for (int i = 0; i < reps; i++) findObject(objs, target);
        long objNs = (System.nanoTime() - t0) / reps;

        t0 = System.nanoTime();
        for (int i = 0; i < reps; i++) findParallel(regNo, target);
        long parNs = (System.nanoTime() - t0) / reps;

        System.out.println("n = " + n + " vehicles, worst-case search by registration number");
        System.out.println("Object design    : " + objNs / 1000 + " microseconds per search  -> O(n)");
        System.out.println("Parallel arrays  : " + parNs / 1000 + " microseconds per search  -> O(n)");
        System.out.println("Found object at index  : " + (findObject(objs, target) != null));
        System.out.println("Found parallel at index: " + findParallel(regNo, target));
        System.out.println();
        System.out.println("Adding a new attribute 'ownerName':");
        System.out.println("  Object design   : add ONE field to VehicleRecord (1 place changes)");
        System.out.println("  Parallel arrays : new String[] ownerName + every insert/delete/swap/sort");
        System.out.println("                    must now update 4 arrays in step (easy to go out of sync)");
    }
}
