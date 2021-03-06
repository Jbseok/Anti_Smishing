package com.example.anti_smishing;
import static com.example.anti_smishing.ApiExplorer.get;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final static int PERMISSON_REQUEST_CODE = 1000;

    //notification

    String permission_list[] = {
            Manifest.permission_group.SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionCheck();

        Intent intent = getIntent();
        new Thread(() -> {
        createNotification(intent);
        }).start();
    }


    private void permissionCheck() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
            ArrayList<String> arrayPermission = new ArrayList<String>();

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                arrayPermission.add(Manifest.permission.RECEIVE_SMS);
            }

            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                arrayPermission.add(Manifest.permission.READ_SMS);
            }

            if (arrayPermission.size() > 0) {
                String strArray[] = new String[arrayPermission.size()];
                strArray = arrayPermission.toArray(strArray);
                ActivityCompat.requestPermissions(this, strArray, PERMISSON_REQUEST_CODE);
            } else {
                //Initialize Code
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSON_REQUEST_CODE: {
                if (grantResults.length < 1) {
                    Toast.makeText(this, "Failed get permission", Toast.LENGTH_SHORT).show();
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    return;
                }

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission is denied : " + permissions[i], Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }

                Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
                //Initialize Code
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //content?????? url?????? ?????????
    public static String extractUrl(String content){
        try {
            String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(content);
            if (m.find()) {
                return m.group();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }


    //?????? ??????
    public static String extraction(String message){
        String malware = "\"result\": \"malware\"";
        String phishing = "\"result\": \"phishing\"";
        String malicious = "\"result\": \"malicious\"";

        String doubt="Url ????????????";
        if(message.indexOf(malware)>=0)
            doubt = doubt + "\n malware ??????";
        else
            doubt = doubt + "\n malware ??????";
        if(message.indexOf(malicious) >= 0)
            doubt = doubt + "\n malicious ??????";
        else
            doubt = doubt + "\n malicious ??????";
        if(message.indexOf(phishing) >= 0)
            doubt = doubt + "\n phishing ??????";
        else
            doubt = doubt + "\n phishing ??????";

        return doubt;

    }

    // ?????? ?????????.
    public void createNotification(Intent intent) {
//        new Thread(() -> {
            if (intent != null) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
                String sender = intent.getStringExtra("sender");
                String content = intent.getStringExtra("content");

                String content_parse_save = extraction(get(extractUrl(content)));

                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle(sender);
//        builder.setContentText("?????? ?????? ?????????");

                builder.setContentText(content_parse_save);

                builder.setColor(Color.RED);
                // ???????????? ?????? ???????????? ?????? ??????
                builder.setAutoCancel(true);

                // ?????? ??????
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(new NotificationChannel("default", "?????? ??????", NotificationManager.IMPORTANCE_DEFAULT));
                }

                // id??????
                // ?????????????????? ??? ????????? ????????? int???
                notificationManager.notify(7, builder.build());

            }
//        }).start();
    }

}