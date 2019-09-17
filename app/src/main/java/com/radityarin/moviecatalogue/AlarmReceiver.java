package com.radityarin.moviecatalogue;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.radityarin.moviecatalogue.activity.MainActivity;
import com.radityarin.moviecatalogue.pojo.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.radityarin.moviecatalogue.BuildConfig.API_KEY;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TYPE_RELEASEREMINDER = "ReleaseReminder";
    public static final String TYPE_DAILYREMINDER = "DailyReminder";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_TYPE = "type";

    private ArrayList<Movie> listMovies = new ArrayList<>();

    private int notifId;

    private final static int ID_DAILYREMINDER = 100;
    private final static int ID_RELEASEREMINDER = 101;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String type = intent.getStringExtra(EXTRA_TYPE);
        if(type.equalsIgnoreCase(TYPE_DAILYREMINDER)) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            String title = context.getResources().getString(R.string.content_title);
            notifId = ID_DAILYREMINDER;
            showAlarmNotification(context,title,message,notifId);
        } else {
            notifId = ID_RELEASEREMINDER;
            checkNewReleaseMovies(new ReleaseMovieCallbacks() {
                @Override
                public void onSuccess(ArrayList<Movie> movies) {
                    listMovies = movies;
                    for (int i = 0; i < listMovies.size(); i++) {
                        showAlarmNotification(context,listMovies.get(i).getTitle(),listMovies.get(i).getTitle()+" has been release today",notifId++);
                    }
                }

                @Override
                public void onFailure(boolean failure) {
                    if(failure) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void showAlarmNotification(Context context, String title, String message, int notifId) {

        String CHANNEL_ID = "Channel_1";
        String CHANNEL_NAME = "AlarmManager channel";

        Intent intent;
            intent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_movie_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, notification);
        }

    }

    public void setRepeatingAlarmDaily(Context context, String type, boolean check) {
        if (check) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(EXTRA_MESSAGE, context.getResources().getString(R.string.content_text));
            intent.putExtra(EXTRA_TYPE, type);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_DAILYREMINDER, intent, 0);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            Toast.makeText(context, context.getResources().getString(R.string.dailyreminder_on), Toast.LENGTH_SHORT).show();
        } else {
            cancelAlarm(context, AlarmReceiver.TYPE_DAILYREMINDER);
        }
    }

    public void setRepeatingAlarmRelease(Context context, String type) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(EXTRA_TYPE, type);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_RELEASEREMINDER, intent, 0);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            Toast.makeText(context, context.getResources().getString(R.string.releasereminder_on), Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(Context context, String type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = type.equalsIgnoreCase(TYPE_RELEASEREMINDER) ? ID_DAILYREMINDER : ID_RELEASEREMINDER;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        pendingIntent.cancel();

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        if(type.equalsIgnoreCase(TYPE_DAILYREMINDER)) {
            Toast.makeText(context, context.getResources().getString(R.string.dailyreminder_off), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.releasereminder_off),Toast.LENGTH_LONG).show();
        }
    }

    private void checkNewReleaseMovies(final ReleaseMovieCallbacks callbacks) {

        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todaydate = simpleDateFormat.format(date);

        AsyncHttpClient client = new AsyncHttpClient();
        final ArrayList<Movie> listItems = new ArrayList<>();
        final String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY + "&primary_release_date.gte="+todaydate+"&primary_release_date.lte="+todaydate;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("results");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject movies = list.getJSONObject(i);
                        Movie movie = new Movie(movies, "movie");
                        listItems.add(movie);
                    }
                    callbacks.onSuccess(listItems);
                    callbacks.onFailure(false);
                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure", error.getMessage());
                callbacks.onFailure(true);
                callbacks.onSuccess(new ArrayList<Movie>());
            }
        });
    }

}
