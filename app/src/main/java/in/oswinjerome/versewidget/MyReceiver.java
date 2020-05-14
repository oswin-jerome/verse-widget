package in.oswinjerome.versewidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        Toast.makeText(context,"Woking",Toast.LENGTH_LONG).show();

        final int min = 0;
        final int max = 80;
        final int random = new Random().nextInt((max - min) + 1) + min;


        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.primary_widget);
        view.setTextViewText(R.id.appwidget_text, get_data(random,context));
        ComponentName theWidget = new ComponentName(context.getApplicationContext(), PrimaryWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        appWidgetManager.updateAppWidget(theWidget,view);

    }

    private  String get_data(int index,Context c){
        String json = null;
        String res = "";
        try {
            InputStream inputStream =  c.getAssets().open("data-eng.json");
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
