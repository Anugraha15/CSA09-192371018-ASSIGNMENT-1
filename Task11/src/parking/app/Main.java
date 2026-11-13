package parking.app;

import parking.model.*;
import parking.service.*;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        ParkingService svc = new ParkingService(new ParkingFacility(new int[]{20}));
        LocalDateTime t = LocalDateTime.of(2026, 11, 11, 9, 0);
        ParkingTicket a = svc.enter(VehicleFactory.create("two_wheeler", "TN01A1"), t);
        ParkingTicket b = svc.enter(VehicleFactory.create("car", "TN02B2"), t.plusMinutes(5));
        ParkingTicket c = svc.enter(VehicleFactory.create("ev", "TN03C3"), t.plusMinutes(10));
        System.out.println(a); System.out.println(b); System.out.println(c);
        System.out.println("Exit TN01A1 fee: Rs " + svc.exit("TN01A1", t.plusMinutes(130), 0));
        System.out.println("Exit TN02B2 fee: Rs " + svc.exit("TN02B2", t.plusMinutes(125), 0));
        System.out.println("Exit TN03C3 fee: Rs " + svc.exit("TN03C3", t.plusMinutes(130), 10) + " (incl. 10 kWh charging)");
        System.out.println("Total collection: Rs " + svc.getTotalCollection() + ", occupied slots: " + svc.getFacility().getOccupied());
    }
}
