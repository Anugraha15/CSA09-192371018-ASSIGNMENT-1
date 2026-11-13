import java.util.ArrayDeque;
import java.util.Deque;

/** Alternative design: free-count per floor + a stack of free slot numbers per floor. */
public class FastFacility {
    private final String[][] slots;
    private final int[] freeCount;
    private final Deque<Integer>[] freeStack;

    @SuppressWarnings("unchecked")
    public FastFacility(int[] slotsPerFloor) {
        int F = slotsPerFloor.length;
        slots = new String[F][];
        freeCount = new int[F];
        freeStack = new Deque[F];
        for (int f = 0; f < F; f++) {
            slots[f] = new String[slotsPerFloor[f]];
            freeCount[f] = slotsPerFloor[f];
            freeStack[f] = new ArrayDeque<>();
            for (int s = slotsPerFloor[f] - 1; s >= 0; s--) freeStack[f].push(s); // slot 0 on top
        }
    }

    /** O(F): skip full floors using freeCount, then O(1) pop. */
    public int[] allocate(String vehicleNo) {
        for (int f = 0; f < slots.length; f++) {
            if (freeCount[f] > 0) {
                int s = freeStack[f].pop();
                slots[f][s] = vehicleNo;
                freeCount[f]--;
                return new int[]{f, s};
            }
        }
        return null;
    }

    /** O(1): validate, clear, push back on the stack. */
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length || slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        freeStack[floor].push(slot);
        freeCount[floor]++;
        return true;
    }
}
