import java.time.LocalTime;
import java.time.Duration;

/** Task 8 - compile-time polymorphism (overloading), dispatch trace, and the instanceof alternative.
 *  Uses Vehicle/Car/TwoWheeler/ElectricCar from FeeDemo.java (same folder). */
public class OverloadDemo {
    // Overload 1: chosen by the compiler when called with (Vehicle, int)
    static double fee(Vehicle v, int minutes) { return v.calculateFee(minutes); }

    // Overload 2: chosen when called with (Vehicle, LocalTime, LocalTime)
    static double fee(Vehicle v, LocalTime in, LocalTime out) {
        int minutes = (int) Duration.between(in, out).toMinutes();
        return v.calculateFee(minutes);
    }

    // Alternative: instanceof chain (what dynamic dispatch lets us avoid)
    static double feeByInstanceOf(Vehicle v, int minutes) {
        int hours = (minutes + 59) / 60;
        if (v instanceof ElectricCar) return hours * 30;
        else if (v instanceof Car) return hours * 30;
        else if (v instanceof TwoWheeler) return hours * 10;
        else throw new IllegalArgumentException("unknown vehicle");
    }

    public static void main(String[] args) {
        Vehicle v = new ElectricCar("TN03C3");        // declared type Vehicle, actual type ElectricCar
        System.out.println("Overload (int)       : " + fee(v, 130));
        System.out.println("Overload (LocalTime) : " + fee(v, LocalTime.of(9, 0), LocalTime.of(11, 10)));

        System.out.println("\nDispatch trace for: v.calculateFee(130)");
        System.out.println("  declared type (compile time) : Vehicle");
        System.out.println("  actual type   (run time)     : " + v.getClass().getSimpleName());
        System.out.println("  calculateFee found in        : Vehicle (inherited, not overridden)");
        System.out.println("  inside it, ratePerHour() runs: Car.ratePerHour() -> 30.0  (late binding)");
        System.out.println("  result                       : " + v.calculateFee(130));

        System.out.println("\ninstanceof alternative gives same answer: " + feeByInstanceOf(v, 130));
    }
}
