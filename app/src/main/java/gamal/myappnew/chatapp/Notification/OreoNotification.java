package gamal.myappnew.chatapp.Notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class OreoNotification  extends ContextWrapper {
    private String CHANEL_NAME="high priority channels";
    private String CHANEL_ID="com.example.notification"+CHANEL_NAME;private NotificationManager notificationManager;
    public OreoNotification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            craeteChanels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void craeteChanels() {

        NotificationChannel notificationChannel=new NotificationChannel(CHANEL_ID,CHANEL_NAME
                , NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("This is Discription");
        notificationChannel.setLightColor(Color.LTGRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manger= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manger.createNotificationChannel(notificationChannel);


    }
    public NotificationManager getManager()
    {
        if (notificationManager==null)
        {
            notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return notificationManager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getorNotification(String title, String body, PendingIntent pendingIntent,
                                                  Uri sounduri, String icon)
    {
        return new Notification.Builder(getApplicationContext(),CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(sounduri);

    }

}
