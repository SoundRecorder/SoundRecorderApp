package com.example.arunnair.soundrecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView time;
    private EditText nameView;
    private Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private String filename = "sample";
    File root = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time = (TextView) findViewById(R.id.time);
        nameView = (EditText) findViewById(R.id.name);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        record = (Button) findViewById(R.id.record);
        stop.setEnabled(false);
        play.setEnabled(false);
        myAudioRecorder = new MediaRecorder();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    0);

        } else {
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            root = android.os.Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath() + "/AudioSamples/Audios");
            if (!file.exists()) {
                file.mkdirs();
            }

            outputFile =  root.getAbsolutePath() + "/AudioSamples/Audios/" + filename + ".mp3";
            Log.d("filename",outputFile);
            myAudioRecorder.setOutputFile(outputFile);
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }

            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filename = nameView.getText().toString();
                    outputFile =  root.getAbsolutePath() + "/AudioSamples/Audios/" + filename + ".mp3";
                    myAudioRecorder.setOutputFile(outputFile);
                    final Handler handler = new Handler();
                    long setMillis = Integer.parseInt(time.getText().toString().substring(0,2))*3600000 + Integer.parseInt(time.getText().toString().substring(3,5))*60000;
                    long millis = 0;
                    Calendar c = Calendar.getInstance();
                    long now = c.getTimeInMillis();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    long currentMillis = now - c.getTimeInMillis();
                    millis = setMillis - currentMillis;
                    Toast.makeText(getApplicationContext(), "Recording will begin in " + millis/1000 + " s", Toast.LENGTH_LONG).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myAudioRecorder.prepare();
                                myAudioRecorder.start();
                            } catch (IllegalStateException ise) {
                                Toast.makeText(getApplicationContext(), "Error in recording", Toast.LENGTH_LONG).show();
                            } catch (IOException ioe) {
                                Toast.makeText(getApplicationContext(), "Error in input", Toast.LENGTH_LONG).show();
                            }
                            record.setEnabled(false);
                            stop.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                        }
                    }, millis);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myAudioRecorder.stop();
                                myAudioRecorder.release();
                                myAudioRecorder = null;
                            } catch (IllegalStateException ise) {
                                Toast.makeText(getApplicationContext(), "Error in stopping", Toast.LENGTH_LONG).show();
                            }
                            record.setEnabled(true);
                            stop.setEnabled(false);
                            play.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                        }
                    }, millis + 5000);
                }
            });

            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                        myAudioRecorder = null;
                    } catch (IllegalStateException ise) {
                        Toast.makeText(getApplicationContext(), "Error in stopping", Toast.LENGTH_LONG).show();
                    }
                    record.setEnabled(true);
                    stop.setEnabled(false);
                    play.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(outputFile);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
            });

        }
    }
