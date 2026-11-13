package parking.app;

import parking.model.StayRecord;
import parking.service.StayService;

public class Task10Demo {
    public static void main(String[] args) {
        StayRecord r = new StayRecord("TN01AB1234", 1, 5);
        r.setDurationMinutes(130);
        r.setKWhUsed(10);
        System.out.println("Valid record: " + r.getRegNo() + " floor " + r.getFloor() + " slot " + r.getSlot()
                + ", " + r.getDurationMinutes() + " min, charging bill Rs " + new StayService().chargingBill(r));

        String[] bad = {"negative duration", "negative slot", "negative kWh"};
        for (String b : bad) {
            try {
                switch (b) {
                    case "negative duration" -> r.setDurationMinutes(-5);
                    case "negative slot"     -> r.setSlot(-1);
                    case "negative kWh"      -> r.setKWhUsed(-3);
                }
            } catch (IllegalArgumentException e) { System.out.println("Rejected (" + b + "): " + e.getMessage()); }
        }
        System.out.println("State after rejected updates is unchanged: duration=" + r.getDurationMinutes() + ", slot=" + r.getSlot());

        // r.durationMinutes = -5;     // would NOT compile: private access
        // r.isLongStay();             // would NOT compile: package-private, we are in parking.app

        System.out.println("\nWrapper-class pitfall:");
        Integer a = 127, b = 127, c = 1000, d = 1000;
        System.out.println("127 == 127   -> " + (a == b) + "   (cached small Integers)");
        System.out.println("1000 == 1000 -> " + (c == d) + "  (different objects!)");
        System.out.println("1000.equals(1000) -> " + c.equals(d));
        StayRecord noCharge = new StayRecord("TN02", 0, 0);
        System.out.println("No charging, null kWh handled -> bill Rs " + new StayService().chargingBill(noCharge));
    }
}
