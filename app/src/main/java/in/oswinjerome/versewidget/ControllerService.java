package in.oswinjerome.versewidget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;

public class ControllerService extends Service {
    public ControllerService() {
    }

    int count = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("TAG", "onStartCommand: ");

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("TAG", "onBind: ");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        final Handler handler = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                Log.d("Handlers", "Called on main thread "+String.valueOf(count));

                if(count==5){
                    final int min = 0;
                    final int max = 99;
                    final int random = new Random().nextInt((max - min) + 1) + min;

                    String time = getCurrentDateTime();

                    RemoteViews view = new RemoteViews(getPackageName(), R.layout.primary_widget);
                    view.setTextViewText(R.id.appwidget_text, get_data(random));
                    ComponentName theWidget = new ComponentName(getApplicationContext(), PrimaryWidget.class);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                    appWidgetManager.updateAppWidget(theWidget,view);

                    count = 0;
                }

                count += 1;


                handler.postDelayed(this, 1 * 60 * 1000);
            }
        };
// Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "in.oswinjerome.verse_widget";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

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



    private String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);

        return hour + ":" + minute;
    }
}
