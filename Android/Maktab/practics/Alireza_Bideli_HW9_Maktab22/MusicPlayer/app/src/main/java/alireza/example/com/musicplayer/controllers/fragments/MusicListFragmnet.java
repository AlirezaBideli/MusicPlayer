package alireza.example.com.musicplayer.controllers.fragments;


import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import alireza.example.com.musicplayer.R;
import alireza.example.com.musicplayer.helpers.FragmentStart;
import alireza.example.com.musicplayer.models.Music;
import alireza.example.com.musicplayer.models.MusicLab;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicListFragmnet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicListFragmnet extends Fragment implements FragmentStart {

    private Bitmap mMusicImg;
    //Widgets variables
    private RecyclerView mRecyMusic;
    //Simple variables
    private String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    private Cursor cursor;
    private MusicLab mMusicLab;

    public MusicListFragmnet() {
        // Required empty public constructor
    }

    public static MusicListFragmnet newInstance() {
        MusicListFragmnet fragment = new MusicListFragmnet();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        initialization(view);

        getDeviceMusics();
        MusicAdpter adpter = new MusicAdpter(mMusicLab.getMusicList());
        mRecyMusic.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyMusic.setHasFixedSize(true);
        mRecyMusic.setAdapter(adpter);
        return view;

    }


    @Override
    public void initialization(View view) {

        mRecyMusic = view.findViewById(R.id.music_recyclerView);
    }

    @Override
    public void setListeners() {

    }

    private void getDeviceMusics() {


        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };

        mMusicLab = MusicLab.getInstance();
        cursor = Objects.requireNonNull(getActivity()).managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while (cursor.moveToNext()) {
            Music music = makeMusicInApp(cursor);
            mMusicLab.addMusic(music);
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

    private void updateMusicImage(ImageView imageView, Uri albumArtUri) {
        try {
            mMusicImg = MediaStore.Images.Media.getBitmap(
                    getActivity().getContentResolver(), albumArtUri);
            mMusicImg = Bitmap.createScaledBitmap(mMusicImg, 250, 250, true);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            mMusicImg = BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.music_default_cover);

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            imageView.setImageBitmap(mMusicImg);
        }
    }


    //mRecyMusic related calsses
    private class MusicHolder extends RecyclerView.ViewHolder {

        private TextView mTxtTitle;
        private TextView mTxtArtist;
        private ImageView mImgMusic;

        public MusicHolder(View itemView) {
            super(itemView);
            mTxtTitle = itemView.findViewById(R.id.title_txt);
            mTxtArtist = itemView.findViewById(R.id.artist_txt);
            mImgMusic = itemView.findViewById(R.id.music_picture);
        }

        public void bind(Music music) {
            mTxtTitle.setText(music.getTittle());
            mTxtArtist.setText(music.getArtist());
            updateMusicImage(mImgMusic,music.getImageUri());

        }
    }

    private class MusicAdpter extends RecyclerView.Adapter<MusicHolder> {

        List<Music> mSongs;

        public MusicAdpter(List<Music> songs) {
            mSongs = songs;
        }

        @Override
        public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.music_list_sample, parent, false);
            return new MusicHolder(view);
        }

        @Override
        public void onBindViewHolder(MusicHolder holder, int position) {

            holder.bind(mSongs.get(position));
        }

        @Override
        public int getItemCount() {
            return mSongs.size();
        }
    }


}
