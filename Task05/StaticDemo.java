import java.lang.ref.WeakReference;

/** Task 5 - static members and object life cycle. */
class Ticket {
    private static int counter;                 // shared by every Ticket in the program
    static final double DAILY_CAP_HOURS;
    static { DAILY_CAP_HOURS = 8; System.out.println("[static block] Ticket class loaded, cap = " + DAILY_CAP_HOURS + " h"); }
    final int ticketNo;
    Ticket() { ticketNo = ++counter; }
    static int issuedSoFar() { return counter; } // static method: needs no object
}

/** WRONG design: the counter is an instance variable of the facility. */
class FacilityWithInstanceCounter {
    private int counter;
    final String name;
    FacilityWithInstanceCounter(String name) { this.name = name; }
    String issue() { return ++counter + ""; }
}

/** Fix 1: keep ONE static counter, add a facility code to make the number readable. */
class FacilityWithStaticCounter {
    private static int counter;
    final String code;
    FacilityWithStaticCounter(String code) { this.code = code; }
    String issue() { return code + "-" + (++counter); }
}

/** Fix 2: independent numbering per facility - instance counter + facility code prefix. */
class FacilityWithPrefix {
    private int counter;
    final String code;
    FacilityWithPrefix(String code) { this.code = code; }
    String issue() { return code + "-" + (++counter); }
}

public class StaticDemo {
    public static void main(String[] args) {
        System.out.println("-- static counter shared by all objects --");
        Ticket t1 = new Ticket(), t2 = new Ticket(), t3 = new Ticket();
        System.out.println("Ticket numbers: " + t1.ticketNo + ", " + t2.ticketNo + ", " + t3.ticketNo
                + "   issued so far (static method) = " + Ticket.issuedSoFar());

        System.out.println("\n-- two facilities, INSTANCE counter (bug) --");
        FacilityWithInstanceCounter north = new FacilityWithInstanceCounter("N"), south = new FacilityWithInstanceCounter("S");
        System.out.println("north issues " + north.issue() + ", south issues " + south.issue() + "  <- duplicate ticket number 1!");

        System.out.println("\n-- Fix 1: static counter + code --");
        FacilityWithStaticCounter n2 = new FacilityWithStaticCounter("N"), s2 = new FacilityWithStaticCounter("S");
        System.out.println(n2.issue() + " " + s2.issue() + " " + n2.issue());

        System.out.println("\n-- Fix 2: per-facility counter + code --");
        FacilityWithPrefix n3 = new FacilityWithPrefix("N"), s3 = new FacilityWithPrefix("S");
        System.out.println(n3.issue() + " " + s3.issue() + " " + n3.issue());

        System.out.println("\n-- object life cycle --");
        Ticket exiting = new Ticket();
        WeakReference<Ticket> ref = new WeakReference<>(exiting);
        exiting = null;                      // vehicle left: last strong reference dropped
        System.gc();                         // only a hint to the JVM
        try { Thread.sleep(100); } catch (InterruptedException e) { }
        System.out.println("Ticket collected after reference dropped? " + (ref.get() == null));

        System.out.println("\n-- pass by value of references --");
        Ticket k = new Ticket();
        int before = k.ticketNo;
        reassign(k);
        System.out.println("After reassign(k), k.ticketNo unchanged: " + (k.ticketNo == before));
    }
    static void reassign(Ticket t) { t = new Ticket(); }   // changes only the local copy of the reference
}
