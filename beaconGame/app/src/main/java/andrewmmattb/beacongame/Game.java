import org.json.JSONObject;

public class Game {
    public int startDate;
    public int endDate;
    public int gameLength;
    public int numPeriods;
    public int periodLength;

    private JSONArray jsonPeriods;

    public Game(String jsonObject){
        final JSONObject obj = new JSONObject(jsonObject);

        this.startDate = obj.getInt("startDate");
        this.endDate = obj.getInt("endDate");
        this.gameLength = obj.getInt("gameLength");
        this.numPeriods = obj.getInt("numPeriods");
        this.periodLength = obj.getInt("periodLength");


        public Period[] periods = new Period[this.numPeriods];
        jsonPeriods = obj.getJSONArray("periods");

        for (int i = 0; i < periods.length; i++) {
            period = new Period(jsonPeriods.getJSONObject(i));
        }
    }
}
