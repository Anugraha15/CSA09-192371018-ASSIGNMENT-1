/** Task 9 - abstract class vs interface, multiple interfaces, default and static interface methods. */
interface Chargeable {
    double RATE_PER_KWH = 12;                       // constant
    double chargingFee(int kWh);                    // abstract capability

    default double chargingFeeWithTax(int kWh) {    // default method: shared behaviour
        return chargingFee(kWh) * 1.18;
    }
    static boolean isValidKwh(int kWh) {            // static method: utility tied to the interface
        return kWh >= 0 && kWh <= 200;
    }
}

interface Payable {                                  // who pays? (fee-paying user)
    double amountDue(double fee);
}

abstract class Vehicle {                              // abstract class: shared STATE + constructor + partial logic
    private final String regNo;
    protected Vehicle(String regNo) { this.regNo = regNo; }
    public String getRegNo() { return regNo; }
    protected abstract double ratePerHour();          // WHAT varies: left abstract
    public double calculateFee(int minutes) { return ((minutes + 59) / 60) * ratePerHour(); }  // HOW: shared
}

class Car extends Vehicle {
    Car(String r) { super(r); }
    @Override protected double ratePerHour() { return 30; }
}

/** One class, three behaviours: is-a Car (class), is Chargeable, is Payable (two interfaces). */
class ElectricCar extends Car implements Chargeable, Payable {
    ElectricCar(String r) { super(r); }
    @Override public double chargingFee(int kWh) { return kWh * RATE_PER_KWH; }
    @Override public double amountDue(double fee) { return fee; }       // visitor pays in full
}

public class AbstractionDemo {
    public static void main(String[] args) {
        // Vehicle v = new Vehicle("X");   // would NOT compile: abstract class cannot be instantiated
        ElectricCar ev = new ElectricCar("TN09EV0001");
        System.out.println("parking fee (130 min)     : " + ev.calculateFee(130));
        System.out.println("charging fee (10 kWh)      : " + ev.chargingFee(10));
        System.out.println("charging fee incl. 18% tax : " + ev.chargingFeeWithTax(10) + "   (default method)");
        System.out.println("isValidKwh(500)            : " + Chargeable.isValidKwh(500) + "   (static interface method)");
        System.out.println("amount due                 : " + ev.amountDue(ev.calculateFee(130) + ev.chargingFee(10)));
        Payable p = ev; Chargeable c = ev; Vehicle v = ev;   // same object, three views
        System.out.println("Same object seen as Payable/Chargeable/Vehicle: " + (p == c && c == v));
    }
}
