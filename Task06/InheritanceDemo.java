/** Task 6 - inheritance hierarchy with super and constructor-order trace. */
class Vehicle {
    private final String regNo;
    protected final double ratePerHour;
    Vehicle(String regNo, double ratePerHour) {
        System.out.println("  1. Vehicle(" + regNo + ") constructor running");
        this.regNo = regNo; this.ratePerHour = ratePerHour;
    }
    String getRegNo() { return regNo; }
    double fee(int hours) { return hours * ratePerHour; }
    @Override public String toString() { return getClass().getSimpleName() + " " + regNo; }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) { super(regNo, 10); System.out.println("  2. TwoWheeler constructor running"); }
}
class Car extends Vehicle {
    protected int seats;
    Car(String regNo) { super(regNo, 30); System.out.println("  2. Car constructor running"); this.seats = 4; }
    Car(String regNo, double rate) { super(regNo, rate); System.out.println("  2. Car constructor running (custom rate)"); }
}
class ElectricCar extends Car {
    private final int batteryKwh;
    ElectricCar(String regNo, int batteryKwh) {
        super(regNo);                          // must be first statement
        System.out.println("  3. ElectricCar constructor running");
        this.batteryKwh = batteryKwh;
    }
    @Override double fee(int hours) { return super.fee(hours) + 0; }   // super.method(): reuse parent's logic
}

/** Users hierarchy (hierarchical inheritance) */
abstract class User {
    final String name;
    User(String name) { this.name = name; }
    abstract boolean parksFree(boolean hasMonthlyPass);
}
class Student extends User { Student(String n) { super(n); } boolean parksFree(boolean p) { return p; } }
class Staff   extends User { Staff(String n)   { super(n); } boolean parksFree(boolean p) { return p; } }
class Visitor extends User { Visitor(String n) { super(n); } boolean parksFree(boolean p) { return false; } }

public class InheritanceDemo {
    public static void main(String[] args) {
        System.out.println("Creating new ElectricCar(\"TN09EV0001\", 40):");
        ElectricCar ev = new ElectricCar("TN09EV0001", 40);
        System.out.println("\nCreating new Car(\"TN02B2\"):");
        Car car = new Car("TN02B2");
        System.out.println("\nInherited members used by ev: regNo=" + ev.getRegNo() + ", fee(2h)=Rs " + ev.fee(2) + ", " + ev);
        System.out.println("\nUsers:");
        User[] users = { new Student("Asha"), new Staff("Ravi"), new Visitor("Guest") };
        for (User u : users) System.out.println("  " + u.getClass().getSimpleName() + " " + u.name + " parks free with a pass? " + u.parksFree(true));
    }
}
