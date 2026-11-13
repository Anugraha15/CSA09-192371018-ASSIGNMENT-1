package parking.app;

import parking.model.*;
import parking.service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

public class TestRunner {
    static int pass = 0, fail = 0;
    static final LocalDate DAY = LocalDate.of(2026, 11, 11);
    static LocalDateTime at(int h, int m) { return DAY.atTime(h, m); }

    /** A brand-new vehicle type written ONLY for this test: proves one new class + one register() line is enough. */
    static class Bus extends Vehicle {
        Bus(String r) { super(r); }
        @Override protected double ratePerHour() { return 80; }
    }

    static void check(String id, String desc, String expected, Supplier<String> action) {
        String actual;
        try { actual = action.get(); } catch (Exception e) { actual = e.getClass().getSimpleName() + ": " + e.getMessage(); }
        boolean ok = expected.equals(actual);
        if (ok) pass++; else fail++;
        System.out.printf("%-4s| %-52s| %-34s| %-34s| %s%n", id, desc, expected, actual, ok ? "PASS" : "FAIL");
    }
    static ParkingService svc() { return new ParkingService(new ParkingFacility(new int[]{5, 5})); }

    public static void main(String[] args) {
        System.out.printf("%-4s| %-52s| %-34s| %-34s| %s%n", "ID", "Test case", "Expected", "Actual", "Result");
        System.out.println("-".repeat(140));

        // ---- Regression: original behaviour must be unchanged ----
        check("R1", "Two-wheeler 130 min (unchanged)", "30.0", () -> "" + new TwoWheeler("A").calculateFee(130));
        check("R2", "Car 130 min (unchanged)", "90.0", () -> "" + new Car("A").calculateFee(130));
        check("R3", "EV 130 min + 10 kWh (unchanged)", "210.0", () -> { ElectricCar e = new ElectricCar("A"); return "" + (e.calculateFee(130) + e.chargingFee(10)); });
        check("R4", "Car 600 min capped (unchanged)", "240.0", () -> "" + new Car("A").calculateFee(600));
        check("R5", "Old addMonthlyPass() still works", "0.0", () -> { ParkingService s = svc(); s.addMonthlyPass("S1"); s.enter(new Car("S1"), at(10, 0)); return "" + s.exit("S1", at(15, 0), 0); });
        check("R6", "Unknown type still rejected", "IllegalArgumentException: Unknown vehicle type: TRUCK", () -> "" + VehicleFactory.create("TRUCK", "T"));

        // ---- E-bike ----
        check("E1", "E-bike 30 min flat", "20.0", () -> "" + new EBike("E").calculateFee(30));
        check("E2", "E-bike 5 hours flat", "20.0", () -> "" + new EBike("E").calculateFee(300));
        check("E3", "E-bike 0 minutes still flat", "20.0", () -> "" + new EBike("E").calculateFee(0));

        // ---- Delivery van (double rate 9-17) ----
        check("V1", "Van 10:00, 120 min (2 peak hours)", "120.0", () -> "" + new DeliveryVan("V").calculateFee(120, at(10, 0)));
        check("V2", "Van 08:00, 120 min (1 off-peak + 1 peak)", "90.0", () -> "" + new DeliveryVan("V").calculateFee(120, at(8, 0)));
        check("V3", "Van 16:30, 90 min (starts peak, 2nd hour 17:30)", "90.0", () -> "" + new DeliveryVan("V").calculateFee(90, at(16, 30)));
        check("V4", "Van 17:00, 60 min (boundary, off-peak)", "30.0", () -> "" + new DeliveryVan("V").calculateFee(60, at(17, 0)));
        check("V5", "Van 09:00, 60 min (boundary, peak)", "60.0", () -> "" + new DeliveryVan("V").calculateFee(60, at(9, 0)));
        check("V6", "Van 0 minutes", "0.0", () -> "" + new DeliveryVan("V").calculateFee(0, at(10, 0)));
        check("V7", "Van 11:00, 600 min capped at 8 h (6 peak + 2 off)", "420.0", () -> "" + new DeliveryVan("V").calculateFee(600, at(11, 0)));

        // ---- Passes ----
        check("P1", "Visitor car with valid one-day pass", "0.0", () -> { ParkingService s = svc(); s.sellPass("VIS", new OneDayPass(DAY)); s.enter(new Car("VIS"), at(9, 0)); return "" + s.exit("VIS", at(14, 0), 0); });
        check("P2", "One-day pass for a different day is ignored", "60.0", () -> { ParkingService s = svc(); s.sellPass("VIS", new OneDayPass(DAY.minusDays(1))); s.enter(new Car("VIS"), at(9, 0)); return "" + s.exit("VIS", at(11, 0), 0); });
        check("P3", "Selling a one-day pass adds Rs 100 to collection", "100.0", () -> { ParkingService s = svc(); s.sellPass("VIS", new OneDayPass(DAY)); return "" + s.getTotalCollection(); });
        check("P4", "Expired monthly pass is charged", "30.0", () -> { ParkingService s = svc(); s.sellPass("S", new MonthlyPass(DAY.minusDays(1))); s.enter(new Car("S"), at(9, 0)); return "" + s.exit("S", at(10, 0), 0); });
        check("P5", "EV with pass: parking free, charging still paid", "120.0", () -> { ParkingService s = svc(); s.sellPass("EV", new OneDayPass(DAY)); s.enter(new ElectricCar("EV"), at(9, 0)); return "" + s.exit("EV", at(11, 0), 10); });

        // ---- Facility: 5 floors, different slot counts ----
        check("F1", "Capacity of {20,30,40,50,60}", "200", () -> "" + new ParkingFacility(new int[]{20, 30, 40, 50, 60}).getCapacity());
        check("F2", "21st vehicle goes to floor 1 slot 0", "[1, 0]", () -> { ParkingFacility f = new ParkingFacility(new int[]{20, 30, 40, 50, 60}); int[] r = null; for (int i = 0; i < 21; i++) r = f.allocate("V" + i); return java.util.Arrays.toString(r); });
        check("F3", "200th vehicle gets last slot [4, 59]", "[4, 59]", () -> { ParkingFacility f = new ParkingFacility(new int[]{20, 30, 40, 50, 60}); int[] r = null; for (int i = 0; i < 200; i++) r = f.allocate("V" + i); return java.util.Arrays.toString(r); });
        check("F4", "201st vehicle -> full (null)", "null", () -> { ParkingFacility f = new ParkingFacility(new int[]{20, 30, 40, 50, 60}); int[] r = null; for (int i = 0; i < 201; i++) r = f.allocate("V" + i); return "" + r; });
        check("F5", "Slot 59 invalid on floor 0 (20 slots)", "false", () -> "" + new ParkingFacility(new int[]{20, 30}).release(0, 59));

        // ---- Extensibility ----
        check("X1", "Unregistered type BUS rejected", "IllegalArgumentException: Unknown vehicle type: BUS", () -> "" + VehicleFactory.create("BUS", "B1"));
        VehicleFactory.register("BUS", Bus::new);   // the ONLY line needed besides the new class
        check("X2", "After register(), BUS 90 min = 2 h x 80", "160.0", () -> "" + VehicleFactory.create("BUS", "B1").calculateFee(90, at(10, 0)));

        // ---- Daily scenario ----
        System.out.println("-".repeat(140));
        ParkingService s = new ParkingService(new ParkingFacility(new int[]{3, 5, 2}));
        s.sellPass("VIS1", new OneDayPass(DAY));                                // +100
        s.enter(VehicleFactory.create("ebike", "TN10E1"), at(9, 0));
        s.enter(VehicleFactory.create("van", "TN11V1"), at(10, 0));
        s.enter(VehicleFactory.create("van", "TN12V2"), at(8, 0));
        s.enter(VehicleFactory.create("car", "VIS1"), at(9, 30));
        s.enter(VehicleFactory.create("ev", "TN13C3"), at(10, 0));
        s.enter(VehicleFactory.create("car", "TN14B4"), at(10, 0));
        s.exit("TN10E1", at(9, 45), 0);      // flat                          = 20
        s.exit("TN11V1", at(12, 0), 0);      // 2 peak hours: 60 + 60         = 120
        s.exit("TN12V2", at(10, 0), 0);      // 08:00 -> 30, 09:00 -> 60      = 90
        s.exit("VIS1", at(14, 30), 0);       // one-day pass                  = 0
        s.exit("TN13C3", at(12, 0), 10);     // 2 x 30 + 10 x 12              = 180
        s.exit("TN14B4", at(11, 1), 0);      // 61 min -> 2 x 30              = 60
        System.out.println("Day scenario hand-calculated: 100 (pass) + 20 + 120 + 90 + 0 + 180 + 60 = Rs 570.0");
        System.out.println("Day scenario program total  : Rs " + s.getTotalCollection());
        boolean ok = s.getTotalCollection() == 570.0;
        if (ok) pass++; else fail++;
        System.out.println("Scenario check: " + (ok ? "PASS" : "FAIL"));
        System.out.println("\nTOTAL: " + pass + " passed, " + fail + " failed");
    }
}
