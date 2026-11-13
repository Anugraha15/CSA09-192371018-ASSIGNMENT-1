import java.util.HashSet;
import java.util.Set;

/** Task 7 - overriding calculateFee/toString/equals/hashCode, final, and the switch alternative. */
abstract class Vehicle {
    private final String regNo;
    public static final int GRACE_MINUTES = 0;                    // final constant
    protected Vehicle(String regNo) { this.regNo = regNo; }
    public String getRegNo() { return regNo; }
    protected abstract double ratePerHour();
    public double calculateFee(int minutes) { return ((minutes + 59) / 60) * ratePerHour(); }
    public final String describe() { return getClass().getSimpleName() + "[" + regNo + "]"; } // final method: cannot be overridden
    @Override public String toString() { return describe() + " Rs" + ratePerHour() + "/h"; }
    @Override public boolean equals(Object o) { return o instanceof Vehicle v && regNo.equals(v.regNo); }
    @Override public int hashCode() { return regNo.hashCode(); }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String r) { super(r); }
    @Override protected double ratePerHour() { return 10; }
}
class Car extends Vehicle {
    Car(String r) { super(r); }
    @Override protected double ratePerHour() { return 30; }
}
class EBike extends Vehicle {                                       // flat fee: overrides calculateFee itself
    EBike(String r) { super(r); }
    @Override protected double ratePerHour() { return 0; }
    @Override public double calculateFee(int minutes) { return 20; }
}
final class ElectricCar extends Car {                               // final class: no further subclassing
    ElectricCar(String r) { super(r); }
}

/** Alternative: ONE class, ONE calculateFee with a switch on a type field. */
class SwitchVehicle {
    final String regNo; final String type;
    SwitchVehicle(String regNo, String type) { this.regNo = regNo; this.type = type; }
    double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        switch (type) {
            case "TWO_WHEELER": return hours * 10;
            case "CAR":         return hours * 30;
            case "EBIKE":       return 20;
            default: throw new IllegalArgumentException("Unknown type " + type);
        }
    }
}

public class OverrideDemo {
    public static void main(String[] args) {
        Vehicle[] all = { new TwoWheeler("A1"), new Car("B2"), new EBike("C3"), new ElectricCar("D4") };
        for (Vehicle v : all) System.out.println(v + "  ->  fee(130 min) = Rs " + v.calculateFee(130));

        System.out.println("\nequals/hashCode:");
        Set<Vehicle> set = new HashSet<>();
        set.add(new Car("TN02B2"));
        set.add(new ElectricCar("TN02B2"));                         // same regNo -> treated as duplicate
        System.out.println("HashSet size after adding two vehicles with same regNo = " + set.size());
        System.out.println("Car(TN02B2).equals(ElectricCar(TN02B2)) = " + new Car("TN02B2").equals(new ElectricCar("TN02B2")));

        System.out.println("\nSwitch alternative: ");
        System.out.println(new SwitchVehicle("B2", "CAR").calculateFee(130));
        try { new SwitchVehicle("Z9", "VAN").calculateFee(60); }
        catch (IllegalArgumentException e) { System.out.println("New type 'VAN' fails at RUNTIME: " + e.getMessage()); }
    }
}
