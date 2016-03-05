package andrewmmattb.beacongame;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by andrewm on 3/5/16.
 */
class MakeSeverPostTask extends AsyncTask<Void, String, Void>
{
        Map<String,Object> params;

    public MakeSeverPostTask(Map<String,Object> params){
        this.params = params;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String jsonString = new JSONObject(this.params).toString();
        try {
            Request.sendPost(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result)
    {

    }
}