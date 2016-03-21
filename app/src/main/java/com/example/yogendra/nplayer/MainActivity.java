package com.example.yogendra.nplayer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yogendra.components.Constants;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Constants cnst=new Constants();
    Cursor cursor;
    public void tmpFindSongs() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while (cursor.moveToNext()) {
            cnst.songs.add(cursor.getString(2));
            File file=new File(cursor.getString(3));
            cnst.fsongs.add(file);
        }
        cnst.Playlist=new String[cnst.fsongs.size()];
        for (int i=0;i!=cnst.fsongs.size();i++){
            cnst.Playlist[i]=cnst.songs.get(i).toString();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)
                findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tmpFindSongs();

        cnst.lv=(ListView) findViewById(R.id.songList);

        ArrayAdapter<String> adp=new ArrayAdapter<String>(getApplicationContext(),R.layout.song_layout,R.id.songtext,cnst.Playlist);
        cnst.lv.setAdapter(adp);
        cnst.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(getApplicationContext(), MainPlayer.class)
                        .putExtra("pos", position)
                        .putExtra("songlist", cnst.fsongs)
                        .putExtra("SongName",cnst.Playlist));
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //Toast.makeText(this, "CALLING FROM CREATE", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id==R.id.action_search){
            Intent intent=new Intent(this,MainPlayer.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

}
