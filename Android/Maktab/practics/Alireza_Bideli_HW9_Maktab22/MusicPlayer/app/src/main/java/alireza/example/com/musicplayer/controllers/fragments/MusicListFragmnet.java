package alireza.example.com.musicplayer.controllers.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import alireza.example.com.musicplayer.R;
import alireza.example.com.musicplayer.helpers.FragmentStart;
import alireza.example.com.musicplayer.models.Music;
import alireza.example.com.musicplayer.models.MusicLab;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MusicListFragmnet extends Fragment implements FragmentStart, View.OnClickListener {


    private static final String BUNDLE_ISPALYING = "isplaying";
    private static final String BUNDLE_PLAY_CLICKED_COUNT = "playclickedCount";
    //Widgets variables
    private RecyclerView mRecyMusic;
    private AppCompatImageView mBtnPlay;
    private AppCompatImageView mBtnNext;
    private ImageView mImgMusic;
    private TextView mTxtTitle;
    private TextView mTxtArtist;
    //Simple variables
    private Music mCurrentMusic;
    private MusicLab mMusicLab;
    private boolean mIsPlaying;
    private short mPlayClickedCount;
    private List<Music> mMusicList;
    private int mMusicPosition = 0;


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
        setListeners();

        setBtnPlayImage();
        fillMusicRecyclerView();
        return view;

    }

    private void fillMusicRecyclerView() {
        mMusicLab.getAndSaveMusics();
        mMusicList=mMusicLab.getMusicList();
        MusicAdpter adpter = new MusicAdpter(mMusicList);
        mRecyMusic.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyMusic.setHasFixedSize(true);
        mRecyMusic.setAdapter(adpter);
    }

    @Override
    public void initialization(View view) {

        mRecyMusic = view.findViewById(R.id.music_recyclerView);
        mBtnPlay = view.findViewById(R.id.music_play);
        mBtnNext = view.findViewById(R.id.music_next);
        mTxtTitle=view.findViewById(R.id.title_txt_musicListFragment);
        mTxtArtist=view.findViewById(R.id.artist_txt_musicListFragment);
        mImgMusic=view.findViewById(R.id.music_picture_musicListFragment);
    }

    @Override
    public void setListeners() {
        mBtnPlay.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.music_play:

                playOrPause();
                break;
            case R.id.music_next:
                moveToNextMusic();
                break;

        }
    }

    private void moveToNextMusic() {
        mMusicPosition++;
        mCurrentMusic=mMusicList.get(mMusicPosition);
        mIsPlaying=false;
        mPlayClickedCount=0;
        try {
            MusicLab.getInstance(getActivity()).playMusic(mCurrentMusic,mIsPlaying);
            fillMusicBar();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    private void setBtnPlayImage() {
        if (mIsPlaying) {
            mBtnPlay.setImageResource(R.drawable.pause);

        } else {
            mBtnPlay.setImageResource(R.drawable.play);

        }
    }

    private void playOrPause() {
        if (mCurrentMusic != null) {
            if (mIsPlaying) {
                mBtnPlay.setImageResource(R.drawable.play);


                mMusicLab.pauseMusic();

                mIsPlaying = false;


            } else {
                mBtnPlay.setImageResource(R.drawable.pause);
                try {

                    mMusicLab.playMusic(mCurrentMusic, checkPlayCount());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mIsPlaying = true;


            }
        }
    }

    private boolean checkPlayCount() {
        mPlayClickedCount++;
        return mPlayClickedCount != 1;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mIsPlaying = savedInstanceState.getBoolean(BUNDLE_ISPALYING);
            mPlayClickedCount = savedInstanceState.getShort(BUNDLE_PLAY_CLICKED_COUNT);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_ISPALYING, mIsPlaying);
        outState.putShort(BUNDLE_PLAY_CLICKED_COUNT, mPlayClickedCount);


    }

    //mRecyMusic related calsses
    private class MusicHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private TextView txtArtist;
        private ImageView imgMusic;

        public MusicHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title_txt);
            txtArtist = itemView.findViewById(R.id.artist_txt);
            imgMusic = itemView.findViewById(R.id.music_picture);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Music previousMusic = mCurrentMusic;

                        mMusicPosition=getAdapterPosition();
                        mCurrentMusic = mMusicList.get(mMusicPosition);
                        if (mCurrentMusic != previousMusic) {
                            mPlayClickedCount = 0;
                            mIsPlaying = false;
                        }
                        fillMusicBar();
                        playOrPause();



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }



        public void bind(Music music) {
            txtTitle.setText(music.getTittle());
            txtArtist.setText(music.getArtist());
            mMusicLab.updateMusicImage(imgMusic, music.getImageUri());





        }
    }
    private void fillMusicBar() {

        mTxtTitle.setText(mCurrentMusic.getTittle());
        mTxtArtist.setText(mCurrentMusic.getArtist());
        MusicLab.getInstance(getActivity()).updateMusicImage(mImgMusic,mCurrentMusic.getImageUri());
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
