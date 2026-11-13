import java.time.LocalDateTime;

/** Task 4 - constructors, this, constructor chaining, copy constructor, factory, setters. */
class ParkingTicket {
    private static long counter = 1000;            // shared by all tickets (see Task 5)

    private final long ticketNo;
    private final String regNo;
    private final int floor, slot;
    private final LocalDateTime entryTime;

    // Master constructor - the only one that really assigns fields
    public ParkingTicket(String regNo, int floor, int slot, LocalDateTime entryTime) {
        if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("regNo required");
        if (floor < 0 || slot < 0) throw new IllegalArgumentException("invalid slot position");
        this.ticketNo = ++counter;
        this.regNo = regNo;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime;
    }

    // Overloaded constructor - chains to the master using this(...)
    public ParkingTicket(String regNo, int floor, int slot) {
        this(regNo, floor, slot, LocalDateTime.now());
    }

    // Copy constructor - duplicate ticket re-issue: SAME ticket number, no counter increment
    public ParkingTicket(ParkingTicket other) {
        this.ticketNo = other.ticketNo;
        this.regNo = other.regNo;
        this.floor = other.floor;
        this.slot = other.slot;
        this.entryTime = other.entryTime;
    }

    // Static factory alternative
    public static ParkingTicket issue(String regNo, int floor, int slot) {
        return new ParkingTicket(regNo, floor, slot);
    }

    public long getTicketNo() { return ticketNo; }
    public String getRegNo() { return regNo; }
    @Override public String toString() {
        return "Ticket#" + ticketNo + " " + regNo + " @(" + floor + "," + slot + ") " + entryTime;
    }
}

/** Alternative: default constructor + setters (object can exist in an INVALID state). */
class MutableTicket {
    long ticketNo; String regNo; int floor, slot;
    public MutableTicket() {}
    public void setRegNo(String r) { regNo = r; }
    public void setFloor(int f) { floor = f; }
    public void setSlot(int s) { slot = s; }
}

public class TicketDemo {
    static Object sink;   // keeps created objects "used" so the JIT cannot delete the loops
    public static void main(String[] args) {
        ParkingTicket a = new ParkingTicket("TN01AB1111", 0, 0, LocalDateTime.of(2026, 11, 11, 9, 0));
        ParkingTicket b = new ParkingTicket("TN02CD2222", 0, 1);            // chained constructor
        ParkingTicket c = new ParkingTicket(a);                             // copy constructor
        ParkingTicket d = ParkingTicket.issue("TN03EF3333", 1, 0);          // static factory
        System.out.println(a);
        System.out.println(b);
        System.out.println(c + "   <- duplicate of a, same ticket number");
        System.out.println(d);
        System.out.println("a == c ? " + (a == c) + "   (different objects on the heap)");
        System.out.println("ticket numbers equal ? " + (a.getTicketNo() == c.getTicketNo()));

        try { new ParkingTicket("", 0, 0); }
        catch (IllegalArgumentException e) { System.out.println("Rejected at construction: " + e.getMessage()); }

        MutableTicket m = new MutableTicket();        // legal, but regNo is null and ticketNo is 0
        System.out.println("MutableTicket right after new: regNo=" + m.regNo + ", ticketNo=" + m.ticketNo + "  <- invalid object");

        // Creation cost of n tickets: constructor vs factory vs default+setters
        int n = 2_000_000;
        for (int w = 0; w < 3; w++) { // warm-up
            for (int i = 0; i < 200000; i++) { new ParkingTicket("X", 0, 0, null); ParkingTicket.issue("X", 0, 0); }
        }
        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++) sink = new ParkingTicket("TN01", 0, 0, null);
        long ctor = (System.nanoTime() - t0) / 1_000_000;
        t0 = System.nanoTime();
        for (int i = 0; i < n; i++) { MutableTicket t = new MutableTicket(); t.setRegNo("TN01"); t.setFloor(0); t.setSlot(0); sink = t; }
        long setter = (System.nanoTime() - t0) / 1_000_000;
        t0 = System.nanoTime();
        for (int i = 0; i < n; i++) sink = ParkingTicket.issue("TN01", 0, 0);
        long factory = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("\nCreating " + n + " tickets (ms): constructor=" + ctor + "  setters=" + setter + "  factory=" + factory + "   -> all O(n) time, O(n) space");
    }
}
