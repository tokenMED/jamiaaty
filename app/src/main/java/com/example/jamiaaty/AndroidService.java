package com.example.jamiaaty;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AndroidService extends Service {
    MediaPlayer player;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUser = "";
    DatabaseReference chatRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final VibrationEffect vibrationEffect1;
            // this effect creates the vibration of default amplitude for 1000ms(1 sec)
            vibrationEffect1 = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);

            // it is safe to cancel other vibrations currently taking place
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect1);
        }

        if(user != null){
            currentUser = user.getUid();
        }

        database.getReference("All Users").child(currentUser).child("notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                NotificationModel model = snapshot.getValue(NotificationModel.class);
                try {

                    if(model!= null && model.getFromName()!= null){
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                            NotificationChannel channel = new NotificationChannel("My Notif","MessageNotif", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel);
                        }
                        String message = model.message.trim().length()>60?model.message.trim().substring(0,60):model.message.trim();
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplication(),"My Notif")
                                .setSmallIcon(R.drawable.ic_baseline_email_24)
                                .setContentTitle(model.getFromName() +" :")
                                .setContentText(message+"...")
                                .setAutoCancel(true);

                        Intent intent1 = new Intent(getApplication(),MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(),0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);

                        NotificationManager notificationManager =  (NotificationManager)getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );
                        if(!ChatActivity.activityRuning){
                            notificationManager.notify(0,builder.build());
                        }
                        database.getReference("All Users").child(currentUser).child("notification").removeValue();
                    }

                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
