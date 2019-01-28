package alireza.example.com.musicplayer.models;

import android.net.Uri;

/**
 * Created by ADMIN on 1/24/2019.
 */

public class Music {

    private String _id;
    private String mArtist;
    private String mTittle;
    private String mData;
    private String mDisplayName;
    private int mDuration;

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    private Uri mImageUri;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getTittle() {
        return mTittle;
    }

    public void setTittle(String tittle) {
        mTittle = tittle;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public enum PlayState
    {
        REPEAT_ONE,
        REPEAT_ALL,
        SHUFFLE

    }

}
