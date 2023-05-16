package ru.startandroid.develop.clickwidget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity: AppCompatActivity() {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private var resultValue: Intent? = null
    private var sp: SharedPreferences? = null
    private var etFormat: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent: Intent = intent
        val extras: Bundle? = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)
        }
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        resultValue = Intent()
        resultValue!!.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)

        setResult(RESULT_CANCELED, resultValue)

        setContentView(R.layout.config)

        sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE)
        etFormat = findViewById<View?>(R.id.etFormat) as EditText
        etFormat!!.setText(sp!!.getString(WIDGET_TIME_FORMAT + widgetID, "HH:mm:ss"))

        val cnt: Int = sp!!.getInt(WIDGET_COUNT + widgetID, -1)
        if (cnt == -1) sp!!.edit().putInt(WIDGET_COUNT + widgetID, 0)
    }

    fun onClick(v: View) {
        sp!!.edit().putString(WIDGET_TIME_FORMAT + widgetID, etFormat!!.text.toString()).commit()
        setResult(RESULT_OK, resultValue)
        finish()
    }

    companion object {
        val WIDGET_PREF = "widget_pref"
        val WIDGET_TIME_FORMAT = "widget_time_format"
        val WIDGET_COUNT = "widget_count_"
    }
}