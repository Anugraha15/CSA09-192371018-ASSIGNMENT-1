package parking.service;

import parking.model.StayRecord;

public class StayService {
    /** public: the service's API. Uses only the public getters of StayRecord. */
    public double chargingBill(StayRecord r) {
        Integer kWh = r.getKWhUsed();           // autoboxed Integer, may be null
        if (kWh == null) return 0;              // avoids NullPointerException from auto-unboxing
        return kWh * 12.0;                      // auto-unboxing here
    }
}
