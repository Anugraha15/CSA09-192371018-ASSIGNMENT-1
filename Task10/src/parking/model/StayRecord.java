package parking.model;

/** Encapsulated record of one stay. Fields are private; validation lives in the setters. */
public class StayRecord {
    private final String regNo;           // private: only this class touches it
    private int floor;
    private int slot;
    private int durationMinutes;
    private Integer kWhUsed;              // wrapper type: null means "did not charge"

    public StayRecord(String regNo, int floor, int slot) {
        if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("regNo required");
        this.regNo = regNo;
        setFloor(floor);
        setSlot(slot);
    }
    public String getRegNo() { return regNo; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public int getDurationMinutes() { return durationMinutes; }
    public Integer getKWhUsed() { return kWhUsed; }

    public void setFloor(int floor) {
        if (floor < 0) throw new IllegalArgumentException("invalid floor " + floor);
        this.floor = floor;
    }
    public void setSlot(int slot) {
        if (slot < 0) throw new IllegalArgumentException("invalid slot " + slot);
        this.slot = slot;
    }
    public void setDurationMinutes(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("duration cannot be negative: " + minutes);
        this.durationMinutes = minutes;
    }
    public void setKWhUsed(Integer kWh) {
        if (kWh != null && kWh < 0) throw new IllegalArgumentException("kWh cannot be negative");
        this.kWhUsed = kWh;
    }
    // package-private: usable only by classes inside parking.model
    boolean isLongStay() { return durationMinutes > 480; }
    // protected: for subclasses and same package
    protected String label() { return regNo + "@" + floor + "/" + slot; }
}
