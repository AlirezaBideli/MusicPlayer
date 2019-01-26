package alireza.example.com.musicplayer.models;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alireza.example.com.musicplayer.R;

public class MusicLab {
    private static final MusicLab instance = new MusicLab();
    private static Context mContext;
    private Bitmap mMusicImg;
    private List<Music> mMusicList = new ArrayList<>();


    private MusicLab() {

    }

    public static MusicLab getInstance(Context context) {
            mContext = context;
        return instance;
    }


    //Methods for Musics

    public List<Music> getMusicList() {
        return mMusicList;
    }

    public void getAndSaveMusics() {

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";


        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while (cursor.moveToNext()) {
            Music music = makeMusicInApp(cursor);
            mMusicList.add(music);
        }
    }

    public void updateMusicImage(ImageView imageView, Uri albumArtUri) {
        try {
            mMusicImg = MediaStore.Images.Media.getBitmap(
                    mContext.getContentResolver(), albumArtUri);
            mMusicImg = Bitmap.createScaledBitmap(mMusicImg, 250, 250, true);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            mMusicImg = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.music_default_cover);

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            imageView.setImageBitmap(mMusicImg);
        }
    }



    private Music makeMusicInApp(Cursor cursor) {

        Music music = new Music();
        music.set_id(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        music.setTittle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        music.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        music.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
        music.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        Long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
        music.setImageUri(albumArtUri);


        return music;
    }


}
