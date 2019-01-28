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
import java.util.Collections;
import java.util.List;

import alireza.example.com.musicplayer.R;

public class MusicLab {
    private static final byte NEXT = 1;
    private static final byte PREVIOUS = 0;
    private static MusicLab instance;
    private Context mContext;
    private Bitmap mMusicImg;
    private List<Music> mMusicList = new ArrayList<>();
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private int mPlayClickedCount;
    private Music mCurrentMusic;
    private int mCurrentPosition;
    //this is used for shuffle and repeat musics
    //this is used with Music.PlayState
    private List<Integer> mPositionList;
    private boolean mIsPlayAgain;

    private MusicLab(Context context) {
        mContext = context.getApplicationContext();
        getAndSaveMusics();
        fillPositionList();

    }

    public static MusicLab getInstance(Context context) {
        if (instance == null)
            instance = new MusicLab(context);
        return instance;
    }

    public boolean isPlayAgain() {
        return mIsPlayAgain;
    }

    public void setPlayAgain(boolean playAgain) {
        mIsPlayAgain = playAgain;
    }

    public List<Integer> getPositionList() {
        return mPositionList;
    }

    private void fillPositionList() {
        mPositionList = new ArrayList<>();
        for (int i = 0; i < mMusicList.size(); i++)
            mPositionList.add(i);
    }

    public Music getCurrentMusic() {

        return mCurrentMusic;
    }

    public void setCurrentMusic(Music currentMusic) {

        mCurrentMusic = currentMusic == null ? mMusicList.get(0) : currentMusic;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }


    public int getPlayClickedCount() {
        return mPlayClickedCount;
    }

    public void setPlayClickedCount(int playClickedCount) {
        mPlayClickedCount = playClickedCount;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
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

    public void updateMusicImage(ImageView imageView, Uri albumArtUri, int width, int height) {
        try {
            mMusicImg = MediaStore.Images.Media.getBitmap(
                    mContext.getContentResolver(), albumArtUri);
            mMusicImg = Bitmap.createScaledBitmap(mMusicImg, width, height, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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


    public void playMusic(Music music, boolean isPlayAgain) throws IOException {

        String filePath = music.getData();

        //Use reset to reinitialized the media player for each time
        //it plays musics and prevent app crashes
        if (!isPlayAgain) {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
        } else
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());


        mMediaPlayer.start();
    }


    public void pauseMusic() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void moveToNextMusic(Music.PlayState playstate) {
        move(playstate, NEXT);
    }

    public void moveToPreviousMusic(Music.PlayState playstate) {

        move(playstate, PREVIOUS);

    }

    private void move(Music.PlayState playState, int actionId) {
        int newPosition;
        if (playState == Music.PlayState.REPEAT_ONE) {
            newPosition = mCurrentPosition;
        } else {
            changeCurrentPosition(actionId);

            List<Integer> positions = choosePositions(playState);
            newPosition = positions.get(mCurrentPosition);
        }
        startPlaying(newPosition);
        mCurrentPosition = newPosition;
    }

    private void startPlaying(int newPosition) {
        mCurrentMusic = mMusicList.get(newPosition);
        mIsPlayAgain = false;
        mPlayClickedCount = 0;
        try {
            playMusic(mCurrentMusic, false);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void changeCurrentPosition(int actionId) {
        if (actionId == NEXT)
            mCurrentPosition++;
        else mCurrentPosition--;
    }


    private List<Integer> getShufflePosList() {
        List<Integer> temp = new ArrayList<>();
        temp.addAll(mPositionList);
        Collections.shuffle(temp);
        return temp;
    }

    private List<Integer> getMainPosList() {
        return mPositionList;
    }


    private List<Integer> choosePositions(Music.PlayState playState) {
        List<Integer> result = new ArrayList<>();
        switch (playState) {

            case REPEAT_ALL:
                result = getMainPosList();
                break;

            case SHUFFLE:
                result = getShufflePosList();

                break;
        }
        return result;
    }


}
