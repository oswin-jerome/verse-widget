package in.oswinjerome.versewidget;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        Intent i= new Intent(this, ControllerService.class);
//        i.putExtra("KEY1", "Value to be used by the service");
//        this.startService(i);


        alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + (5*60*1000),
                (5*60*1000), alarmIntent);

//        Log.d("TAG", "onCreate: "+alarmMgr.getNextAlarmClock().getTriggerTime());


        Button createWidget =  (Button) findViewById(R.id.create_widget);

        createWidget.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AppWidgetManager mAppWidgetManager = getSystemService(AppWidgetManager.class);

                    ComponentName myProvider = new ComponentName(MainActivity.this, PrimaryWidget.class);

                    Bundle b = new Bundle();
                    b.putString("ggg", "ggg");
                    assert mAppWidgetManager != null;
                    if (mAppWidgetManager.isRequestPinAppWidgetSupported()) {


                        Intent pinnedWidgetCallbackIntent = new Intent(MainActivity.this, PrimaryWidget.class);
                        PendingIntent successCallback = PendingIntent.getBroadcast(MainActivity.this, 0,
                                pinnedWidgetCallbackIntent, 0);

                        mAppWidgetManager.requestPinAppWidget(myProvider, b, successCallback);

                    }else{
                        Toast.makeText(getApplicationContext(),"Add widget manually",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Add widget manually",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);

        return hour + ":" + minute;
    }

    public void pap(){}


    private  String get_data(int index){
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

