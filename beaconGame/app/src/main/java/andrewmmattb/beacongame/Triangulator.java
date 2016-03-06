package andrewmmattb.beacongame;

import org.altbeacon.beacon.BeaconManager;

/**
 * Created by andrewm on 3/6/16.
 */
public class Triangulator {

    public double getDistances(double[] values){
        double totalRSSI = 0;
        for (int i = 0; i < values.length; i++) {
            totalRSSI += values[i];
        }
        double averageRSSI = totalRSSI/(values.length*1.25);
        return averageRSSI;
    }

}
