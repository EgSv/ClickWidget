package ru.startandroid.develop.clickwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import java.text.SimpleDateFormat
import java.util.*

class MyWidget: AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (i in appWidgetIds!!) {
            updateWidget(context!!, appWidgetManager!!, i)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        val editor: SharedPreferences.Editor = context!!.getSharedPreferences(
            ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit()
        for (widgetID in appWidgetIds!!) {
            editor.remove(ConfigActivity.WIDGET_TIME_FORMAT + widgetID)
            editor.remove(ConfigActivity.WIDGET_COUNT + widgetID)
        }
        editor.commit()
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent!!.action.equals(ACTION_CHANGE, ignoreCase = true)) {
            var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
            val extras = intent.extras
            if (extras != null) {
                mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val sp: SharedPreferences = context!!.getSharedPreferences(
                    ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE)
                val cnt: Int = sp.getInt(ConfigActivity.WIDGET_COUNT + mAppWidgetId, 0)
                sp.edit().putInt(ConfigActivity.WIDGET_COUNT + mAppWidgetId, + cnt).commit()

                updateWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId)
            }
        }
    }

    companion object {
        const val ACTION_CHANGE = "ru.startandroid.develop.p1201clickwidget.change_count"
        fun updateWidget(
            ctx: Context, appWidgetManager: AppWidgetManager,
            widgetID: Int,
        ) {
            val sp = ctx.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE)

            val timeFormat = sp.getString(ConfigActivity.WIDGET_TIME_FORMAT
                    + widgetID, null) ?: return
            val sdf = SimpleDateFormat(timeFormat)
            val currentTime: String = sdf.format(Date(System.currentTimeMillis()))

            val count = sp.getInt(ConfigActivity.WIDGET_COUNT
                    + widgetID, 0).toString()

            // Помещаем данные в текстовые поля
            val widgetView = RemoteViews(ctx.packageName,
                R.layout.widget)
            widgetView.setTextViewText(R.id.tvTime, currentTime)
            widgetView.setTextViewText(R.id.tvCount, count)

            val configIntent = Intent(ctx, ConfigActivity::class.java)
            configIntent.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            var pIntent = PendingIntent.getActivity(ctx, widgetID,
                configIntent, 0)
            widgetView.setOnClickPendingIntent(R.id.tvPressConfig, pIntent)

            val updateIntent = Intent(ctx, MyWidget::class.java)
            updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetID))
            pIntent = PendingIntent.getBroadcast(ctx, widgetID, updateIntent, 0)
            widgetView.setOnClickPendingIntent(R.id.tvPressUpdate, pIntent)

            val countIntent = Intent(ctx, MyWidget::class.java)
            countIntent.action = ACTION_CHANGE
            countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            pIntent = PendingIntent.getBroadcast(ctx, widgetID, countIntent, 0)
            widgetView.setOnClickPendingIntent(R.id.tvPressCount, pIntent)

            appWidgetManager.updateAppWidget(widgetID, widgetView)
        }
    }
}