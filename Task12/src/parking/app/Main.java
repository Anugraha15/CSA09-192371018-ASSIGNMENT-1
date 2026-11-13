package parking.app;

import parking.model.*;
import parking.service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // 5 floors with DIFFERENT slot counts
        ParkingService svc = new ParkingService(new ParkingFacility(new int[]{20, 30, 40, 50, 60}));
        LocalDate day = LocalDate.of(2026, 11, 11);
        svc.sellPass("VIS1", new OneDayPass(day));
        ParkingTicket t1 = svc.enter(VehicleFactory.create("ebike", "TN10E1"), day.atTime(9, 0));
        ParkingTicket t2 = svc.enter(VehicleFactory.create("van", "TN11V1"), day.atTime(10, 0));
        ParkingTicket t3 = svc.enter(VehicleFactory.create("car", "VIS1"), day.atTime(10, 30));
        System.out.println(t1); System.out.println(t2); System.out.println(t3);
        System.out.println("E-bike fee : Rs " + svc.exit("TN10E1", day.atTime(13, 0), 0));
        System.out.println("Van fee    : Rs " + svc.exit("TN11V1", day.atTime(12, 0), 0));
        System.out.println("Visitor car (one-day pass): Rs " + svc.exit("VIS1", day.atTime(15, 0), 0));
        System.out.println("Total collection (incl. Rs 100 pass sale): Rs " + svc.getTotalCollection());
    }
}
