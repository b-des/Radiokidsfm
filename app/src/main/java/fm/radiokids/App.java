package fm.radiokids;

import android.app.Application;
import android.content.Context;

import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fm.radiokids.interfaces.RadioKidsApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static RadioKidsApi radioKidsApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://prj2.b-des.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        radioKidsApi = retrofit.create(RadioKidsApi.class);
    }

    public static RadioKidsApi getApi() {
        return radioKidsApi;
    }

    public static String getDate(Context context, String timestamp){

        /*long now = System.currentTimeMillis()/1000;*/
            long time = Long.valueOf(timestamp);
            String fd = "";
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            DateTimeUtils.setTimeZone(tz.getID());
            Date date = DateTimeUtils.formatDate(time, DateTimeUnits.SECONDS);


        /*long diff = now - timestamp;
        if(diff < 60){
            fd = String.valueOf(diff)+"";
        }
        else */if(DateTimeUtils.isToday(date)){
            fd = DateTimeUtils.formatWithPattern(date," HH:mm");
        }
        else if(DateTimeUtils.isYesterday(date)){
            fd = context.getResources().getString(R.string.text_yesterday)+DateTimeUtils.formatWithPattern(date," HH:mm");
        }else{
            fd =  DateTimeUtils.formatWithPattern(date,"dd/MM HH:mm");
        }
            return  fd;

    }

    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false; }
}
