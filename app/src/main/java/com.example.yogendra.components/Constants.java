package com.example.yogendra.components;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.yogendra.nplayer.MainActivity;
import com.example.yogendra.nplayer.MainPlayer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Yogendra on 09-02-2016.
 */
public class Constants {
        //Defining Variables For The Player
        public static SeekBar sb;
        public static int custProgress;
        public static Button btnPlay,btnNext,btnPrev;
        public static int position;
        public static String stmp,arttmp;
        public static TextView songTitle,sartist;
        public static MediaMetadataRetriever mmr;
        public static Uri u;
        public static byte[] art;
        public static MediaPlayer mp;


        public static ArrayList<File>fsongs=new ArrayList<File>();
        public static ArrayList<String>songs=new ArrayList<String>();
        public static ArrayList<File> mySongs;
        public static String [] Playlist;

        public static ImageView album_art;
        public static Bitmap sImage;
        public static ListView lv;
        public Notification noti;
        public static Intent intent,nintent;
        public PendingIntent pIntent;
        public static int mCurrentPosition;
        public static final Handler mHandler = new Handler();
}
