public class Period{
    public int num;
    public int startDate;
    public int endDate;
    public int[] beaconPointMultipliers;

    public Period(JSONObject period){
        this.num = period.getInt("num");
        this.startDate = period.getInt("num");
        this.endDate = period.getInt("num");
        this.beaconPointMultipliers = period.getArray("beaconPointMultipliers");
    }
}
