package in.oswinjerome.versewidget;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Helper extends AppCompatActivity {
    public   String get_data(int index){
        String json = null;
        String res = "";
        try {

            InputStream inputStream = getAssets().open("data-eng.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray events = jsonObject.getJSONArray("data");
            res =   events.getString(index);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

}
