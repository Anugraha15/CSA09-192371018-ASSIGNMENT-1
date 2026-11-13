package parking.app;

import java.util.*;

/** Task 11 alternative: find an active ticket by registration number on exit. */
public class Benchmark {
    record T(String regNo, int slot) {}
    static Object sink;

    public static void main(String[] args) {
        int[] sizes = {1_000, 10_000, 100_000};
        System.out.println("n        | array scan (ns) | ArrayList.indexOf (ns) | HashMap.get (ns)   [worst case: last element]");
        for (int n : sizes) {
            T[] arr = new T[n]; List<T> list = new ArrayList<>(); Map<String, T> map = new HashMap<>();
            for (int i = 0; i < n; i++) { T t = new T("TN" + i, i); arr[i] = t; list.add(t); map.put(t.regNo(), t); }
            String key = "TN" + (n - 1);
            T keyObj = new T(key, n - 1);
            int reps = Math.max(200, 2_000_000 / n);
            for (int w = 0; w < 3000; w++) { scan(arr, key); list.indexOf(keyObj); map.get(key); }
            long t0 = System.nanoTime(); for (int i = 0; i < reps; i++) sink = scan(arr, key);
            long a = (System.nanoTime() - t0) / reps;
            t0 = System.nanoTime(); for (int i = 0; i < reps; i++) sink = list.indexOf(keyObj);
            long l = (System.nanoTime() - t0) / reps;
            t0 = System.nanoTime(); for (int i = 0; i < reps * 100; i++) sink = map.get(key);
            long m = (System.nanoTime() - t0) / (reps * 100L);
            System.out.printf("%-8d | %15d | %22d | %16d%n", n, a, l, m);
        }
        Runtime rt = Runtime.getRuntime();
        System.out.println("\nMemory for n = 100000 parked vehicles (approx., measured after GC):");
        System.out.println("  array      : " + usedKb(() -> { T[] x = new T[100_000]; for (int i = 0; i < x.length; i++) x[i] = new T("TN" + i, i); return x; }) + " KB");
        System.out.println("  ArrayList  : " + usedKb(() -> { List<T> x = new ArrayList<>(); for (int i = 0; i < 100_000; i++) x.add(new T("TN" + i, i)); return x; }) + " KB");
        System.out.println("  HashMap    : " + usedKb(() -> { Map<String, T> x = new HashMap<>(); for (int i = 0; i < 100_000; i++) { T t = new T("TN" + i, i); x.put(t.regNo(), t); } return x; }) + " KB");
    }
    static T scan(T[] arr, String key) { for (T t : arr) if (t.regNo().equals(key)) return t; return null; }
    static long usedKb(java.util.function.Supplier<Object> build) {
        System.gc(); try { Thread.sleep(100); } catch (InterruptedException e) {}
        Runtime rt = Runtime.getRuntime(); long before = rt.totalMemory() - rt.freeMemory();
        Object o = build.get();
        System.gc(); try { Thread.sleep(100); } catch (InterruptedException e) {}
        long after = rt.totalMemory() - rt.freeMemory();
        sink = o; return Math.max(0, (after - before) / 1024);
    }
}
