package alireza.example.com.musicplayer.controllers.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import alireza.example.com.musicplayer.R;
import alireza.example.com.musicplayer.helpers.FragmentStart;
import alireza.example.com.musicplayer.models.Music;
import alireza.example.com.musicplayer.models.MusicLab;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicListFragmnet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicListFragmnet extends Fragment implements FragmentStart {


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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicLab = MusicLab.getInstance(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        initialization(view);


        fiilMusicRecyclerView();
        return view;

    }

    private void fiilMusicRecyclerView() {
        mMusicLab.getAndSaveMusics();
        MusicAdpter adpter = new MusicAdpter(mMusicLab.getMusicList());
        mRecyMusic.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyMusic.setHasFixedSize(true);
        mRecyMusic.setAdapter(adpter);
    }


    @Override
    public void initialization(View view) {

        mRecyMusic = view.findViewById(R.id.music_recyclerView);
    }

    @Override
    public void setListeners() {

    }


    //mRecyMusic related calsses
    private class MusicHolder extends RecyclerView.ViewHolder {

        private TextView mTxtTitle;
        private TextView mTxtArtist;
        private ImageView mImgMusic;
        private Music mCurrentMusic;

        public MusicHolder(View itemView) {
            super(itemView);
            mTxtTitle = itemView.findViewById(R.id.title_txt);
            mTxtArtist = itemView.findViewById(R.id.artist_txt);
            mImgMusic = itemView.findViewById(R.id.music_picture);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mMusicLab.playMusic(mCurrentMusic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void bind(Music music) {
            mTxtTitle.setText(music.getTittle());
            mTxtArtist.setText(music.getArtist());
            mMusicLab.updateMusicImage(mImgMusic, music.getImageUri());
            mCurrentMusic = music;
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
