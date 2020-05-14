package in.oswinjerome.versewidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import top.defaults.colorpicker.ColorPickerPopup;

/**
 * The configuration screen for the {@link PrimaryWidget PrimaryWidget} AppWidget.
 */
public class PrimaryWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "in.oswinjerome.versewidget.PrimaryWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = PrimaryWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
//            saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            PrimaryWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    View.OnClickListener fontColorClick = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = PrimaryWidgetConfigureActivity.this;
            new ColorPickerPopup.Builder(getApplicationContext())
                    .initialColor(Color.WHITE) // Set initial color
                    .enableBrightness(true) // Enable brightness slider or not
                    .enableAlpha(true) // Enable alpha slider or not
                    .okTitle("Choose")
                    .cancelTitle("Cancel")
                    .showIndicator(true)
                    .showValue(true)
                    .build()
                    .show(v, new ColorPickerPopup.ColorPickerObserver() {
                        @Override
                        public void onColorPicked(int color) {
//                            v.setBackgroundColor(color);
                            Log.d("TAG", "onColorPicked: "+color);
                            saveFontColor(context,mAppWidgetId,color);
                            findViewById(R.id.pick_fcolor).setBackgroundColor(color);
                        }
                    });
        }
    };

    View.OnClickListener backColorClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = PrimaryWidgetConfigureActivity.this;
            new ColorPickerPopup.Builder(getApplicationContext())
                    .initialColor(Color.argb(40,0,0,0)) // Set initial color
                    .enableBrightness(true) // Enable brightness slider or not
                    .enableAlpha(true) // Enable alpha slider or not
                    .okTitle("Choose")
                    .cancelTitle("Cancel")
                    .showIndicator(true)
                    .showValue(false)
                    .build()
                    .show(v, new ColorPickerPopup.ColorPickerObserver() {
                        @Override
                        public void onColorPicked(int color) {
//                            v.setBackgroundColor(color);
                            Log.d("TAG", "onColorPicked: "+color);
                            saveBackColor(context,mAppWidgetId,color);
                            findViewById(R.id.pick_bcolor).setBackgroundColor(color);
                        }
                    });
        }
    };

    public PrimaryWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static void saveFontColor(Context context, int appWidgetId, int color) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY +"FC"+ appWidgetId, color);
        prefs.apply();
    }
    static void saveBackColor(Context context, int appWidgetId, int color) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY +"BC"+ appWidgetId, color);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }
    static int loadBCPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int titleValue = prefs.getInt(PREF_PREFIX_KEY +"BC"+ appWidgetId, 0);
        if (titleValue != 0) {
            return titleValue;
        } else {
            return Color.argb(0,0,0,0);
        }
    }
    static int loadFCPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int titleValue = prefs.getInt(PREF_PREFIX_KEY +"FC"+ appWidgetId, 0);
        if (titleValue != 0) {
            return titleValue;
        } else {
            return Color.argb(255,255,255,255);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.primary_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
        findViewById(R.id.pick_fcolor).setOnClickListener(fontColorClick);
        findViewById(R.id.pick_bcolor).setOnClickListener(backColorClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

//        mAppWidgetText.setText(loadTitlePref(PrimaryWidgetConfigureActivity.this, mAppWidgetId));
    }
}

