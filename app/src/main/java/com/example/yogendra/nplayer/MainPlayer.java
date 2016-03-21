package com.example.yogendra.nplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.example.yogendra.components.*;


/**
 * Created by Yogendra on 30-01-2016.
 */

public class MainPlayer extends Activity implements View.OnClickListener{
    Constants cnst=new Constants();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("MainPlayer", "OnCReate");
        setContentView(R.layout.mainplayer);
        if ((getIntent().getFlags()&Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)!=0){
           // Log.i("MainPlayer","BroughtToFront");
            if(cnst.mp!=null) {
             //   Log.i("MainPlayer", "CallingGetFlags");
                cnst.stmp = cnst.songTitle.getText().toString();
                cnst.arttmp = cnst.sartist.getText().toString();
                getInit();
                restoreStatus();
            }
        }else
        {
          //  Log.i("MainPlayer","StartingPlayer");

            if (cnst.mp != null) {
            //    System.out.println("Resetting_Player..");
                cnst.mp.stop();
                cnst.mp.release();
            }

            //Initialize All Items
            getInit();

            //Fetch The Resource From The Sdcard
            Intentparser();

            //Apply Image And The Title
            stitleArtist();

            cnst.mp = MediaPlayer.create(getApplicationContext(), cnst.u);
            //System.out.println("Duration::" + mp.getDuration());
            cnst.sb.setMax(cnst.mp.getDuration());

            cnst.mp.start();

            initNotification();

            upskbar();

        }
    }

    public void SeekarChanged(){
        cnst.sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                cnst.custProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                customStopTouchTracking(cnst.custProgress);
            }

            public void customStopTouchTracking(int progress) {
                if (cnst.mp != null) {
                    cnst.mp.seekTo(progress);
                }
            }

        });
    }
    public void restoreStatus() {
        if (cnst.mp != null) {
          //  Log.i("MainPlayer", "RestoringSession...");
            cnst.songTitle.setText(cnst.stmp);
            cnst.sartist.setText(cnst.arttmp);
            cnst.sb.setMax(cnst.mp.getDuration());
            cnst.mCurrentPosition = cnst.mp.getCurrentPosition();
            cnst.sb.setProgress(cnst.mCurrentPosition);
            SeekarChanged();
            try{
                cnst.album_art.setImageBitmap(cnst.sImage);
            }catch(Exception ex) {
                cnst.sImage=BitmapFactory.decodeResource(getResources(),R.drawable.noimage);
                cnst.album_art.setImageBitmap(cnst.sImage);
            }
         //   Log.i("MainPlayer", "RestoreComplete...");
        }
    }

    public void getInit(){

        //Intializing Object

        //Log.i("MainPlayer","Initializing all button..");

        cnst.btnPrev=(Button)findViewById(R.id.btnprev);
        cnst.btnPlay=(Button)findViewById(R.id.btnPP);
        cnst.btnNext=(Button)findViewById(R.id.btnNext);

        cnst.songTitle=(TextView)findViewById(R.id.sTitle);
        cnst.sartist=(TextView)findViewById(R.id.sArtist);
        cnst.album_art=(ImageView)findViewById(R.id.ArtView);

        cnst.sb=(SeekBar)findViewById(R.id.skBPlayer);

        cnst.btnPlay.setOnClickListener(this);
        cnst.btnNext.setOnClickListener(this);
        cnst.btnPrev.setOnClickListener(this);

        //Log.i("MainPlayer", "InitializingComplete..");

    }
    public void Intentparser(){

        //Log.i("MainPlayer","StartingIntentParser..");
        //Parsing The Intent
        Intent i=getIntent();
        cnst.Playlist=i.getStringArrayExtra("SongName");
        cnst.mySongs=(ArrayList)i.getIntegerArrayListExtra("songlist");
        cnst.position=i.getIntExtra("pos", 0);
        cnst.mmr = new MediaMetadataRetriever();
        cnst.u=Uri.parse(cnst.mySongs.get(cnst.position).toString());
        cnst.mmr.setDataSource(getApplicationContext(), cnst.u);
        //Log.i("MainPlayer", "CompleteIntentParser..");
    }
    public void stitleArtist(){
        //Log.i("MainPlayer","ApplyingArtist");
        try{

            try{
                //Checking if album key is there or not
                cnst.songTitle.setText(cnst.mmr.extractMetadata(MediaMetadataRetriever
                        .METADATA_KEY_TITLE).toString());
                cnst.sartist.setText(cnst.mmr.extractMetadata(MediaMetadataRetriever
                        .METADATA_KEY_ALBUM).toString());
                notfiCreator(cnst.songTitle.getText().toString(),cnst.sartist.getText().toString());

            }catch (Exception ex){
                //setting both TITLE text to title
                cnst.songTitle.setText(cnst.mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        .toString());
                cnst.sartist.setText(cnst.mmr.extractMetadata(MediaMetadataRetriever
                        .METADATA_KEY_TITLE).toString());
                notfiCreator(cnst.songTitle.getText().toString(), cnst.sartist.getText().toString());
            }
        }catch (Exception ex){

            cnst.songTitle.setText(cnst.mySongs.get(cnst.position)
                    .getName().toString());
            cnst.sartist.setText(cnst.mySongs.get(cnst.position)
                    .getName().toString());
            notfiCreator(cnst.songTitle.getText().toString(), cnst.sartist.getText().toString());
        }

        try{

            //Setting Artist Image
            cnst.art=cnst.mmr.getEmbeddedPicture();
            cnst.sImage=BitmapFactory.decodeByteArray(cnst.art,0,cnst.art.length);
            cnst.album_art.setImageBitmap(cnst.sImage);

        }catch (Exception ex){

            //Catching If Not There
            cnst.sImage=BitmapFactory.decodeResource(getResources(),R.drawable.noimage);
            cnst.album_art.setImageBitmap(cnst.sImage);

        }
        //Log.i("MainPlayer","CompleteArtistApply");
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnPP:

                if (cnst.mp.isPlaying()){

                    cnst.mp.pause();
                    cnst.btnPlay.setBackgroundResource(R.drawable.playbtn);
                    //Log.i("MainPlayer","Pause_Player..");
                }
                else {

                    cnst.btnPlay.setBackgroundResource(R.drawable.pausebtn);
                    cnst.mp.start();
                    //Log.i("MainPlayer","Resuming Player..");

                }

                break;

            case R.id.btnprev:

                //Log.i("MainPlayer","Calling Prev Song..");
                cnst.mp.stop();
                cnst.mp.release();
                --cnst.position;
                SongHandler();
                break;

            case R.id.btnNext:

                //Log.i("MainPlayer","Calling Next SOng..");
                cnst.mp.stop();
                cnst.mp.release();
                ++cnst.position;
                SongHandler();
                break;
        }
    }

    public void upskbar(){
      //  Log.i("MainPlayer", "UpdatingSeekBar..");

        //Make sure you update Seekbar on UI thread
        MainPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cnst.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        ++cnst.position;
                        SongHandler();
                    }
                });

                if (cnst.mp != null) {
                    cnst.mCurrentPosition = cnst.mp.getCurrentPosition();
                    cnst.sb.setProgress(cnst.mCurrentPosition);
                }
                cnst.mHandler.postDelayed(this, 1000);
            }
        });
        SeekarChanged();
      //  Log.i("MainPlayer", "CompleteSeekBar..");
    }

    public void SongHandler(){

       // Log.i("MainPlayer","StartingSongHandler..");

      //  System.out.println("Position::" + cnst.position + "Song_List_Size::" + (cnst.mySongs.size() - 1));
        if (cnst.position<0) {
            //System.out.println("Calling <0....");
            cnst.position = (cnst.mySongs.size()-1);
            cnst.u=Uri.parse(cnst.mySongs.get(cnst.position).toString());
        }
        else if(cnst.position==cnst.mySongs.size()) {
            //System.out.println("Reach To size....");
            cnst.position = 0;
            cnst.u=Uri.parse(cnst.mySongs.get(cnst.position).toString());
        }else {
            //System.out.println("Other Condition....");
            cnst.u = Uri.parse(cnst.mySongs.get(cnst.position).toString());

        }
        cnst.mp = MediaPlayer.create(getApplicationContext(), cnst.u);
        cnst.mmr.setDataSource(getApplicationContext(), cnst.u);
        cnst.sb.setMax(cnst.mp.getDuration());

        stitleArtist();

        cnst.btnPlay.setBackgroundResource(R.drawable.pausebtn);

        cnst.mp.start();

      //  Log.i("MainPlayer", "CompleteSongHandler..");
    }

    public void initNotification(){
       // Log.i("MainPlayer","Calling Initialzing Notfication..");
        cnst.pIntent=PendingIntent.getBroadcast(this,0,new Intent(this,ButtonReciever.class),0);
    }

    public void notfiCreator(String Title,String artist){
        NotificationCompat.Builder mb = new NotificationCompat.Builder(getBaseContext());
        mb.setSmallIcon(R.drawable.n);
        mb.setContentTitle(Title);
        mb.setContentText(artist);
        mb.setPriority(NotificationCompat.PRIORITY_LOW);
        mb.setOngoing(true);
        mb.setTicker(Title);
        mb.setShowWhen(true);
        mb.setContentIntent(cnst.pIntent);
        mb.addAction(R.drawable.prevbtn,"",cnst.pIntent);
        mb.addAction(R.drawable.playbtn, "", cnst.pIntent);
        mb.addAction(R.drawable.nextbtn,"",cnst.pIntent);
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) getSystemService(ns);
        manager.notify(1, mb.build());
    }
}