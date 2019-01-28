package alireza.example.com.musicplayer.controllers.fragments;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

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


    //CallBack
    private CallBacks mCallBacks;
    //Widgets variables
    private RecyclerView mRecyMusic;
    private MaterialButton mBtnPlay;
    private MaterialButton mBtnNext;
    private ImageView mImgMusic;
    private TextView mTxtTitle;
    private TextView mTxtArtist;
    //Simple variables
    private MusicLab mMusicLab;
    private List<Music> mMusicList;
    private Music mCurrentMusic;
    private boolean mIsPlaying;
    private int mPlayClickedCount;
    private int mMusicPosition;
    private int imgWidth = 100;
    private int imgHeight = 100;
    private Music.PlayState mPlayState;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CallBacks)
            mCallBacks = (CallBacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMusicLab = MusicLab.getInstance(getActivity());
        mMusicList = mMusicLab.getMusicList();
        mMusicPosition = mMusicLab.getCurrentPosition();
        mCurrentMusic = mMusicList.get(mMusicPosition);
        mPlayClickedCount = mMusicLab.getPlayClickedCount();
        mIsPlaying = mMusicLab.isPlaying();

        mPlayState= Music.PlayState.REPEAT_ALL;


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        initialization(view);
        setListeners();


        return view;

    }


    @Override
    public void onStart() {
        super.onStart();
        setBtnPlayImage();
        fillMusicRecyclerView();
        fillMusicBar();
    }

    private void fillMusicRecyclerView() {

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
        mTxtTitle = view.findViewById(R.id.title_txt_musicListFragment);
        mTxtArtist = view.findViewById(R.id.artist_txt_musicListFragment);
        mImgMusic = view.findViewById(R.id.music_picture_musicListFragment);
    }

    @Override
    public void setListeners() {
        mBtnPlay.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mImgMusic.setOnClickListener(this);

        mMusicLab.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                moveToNext();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.music_play:
                playOrPause();
                break;
            case R.id.music_next:
                moveToNext();
                break;
            case R.id.music_picture_musicListFragment:
                mCallBacks.goToMusicPage();
                break;


        }
    }

    private void moveToNext() {
        mMusicLab.moveToNextMusic(mPlayState);
        mMusicPosition=mMusicLab.getCurrentPosition();
        mPlayClickedCount = 0;
        mIsPlaying = false;
        mCurrentMusic = mMusicLab.getCurrentMusic();
        fillMusicBar();
    }


    private void setBtnPlayImage() {
        if (mIsPlaying) {
            mBtnPlay.setIconResource(R.drawable.pause);

        } else {
            mBtnPlay.setIconResource(R.drawable.play);

        }
    }

    private void playOrPause() {
        if (mCurrentMusic != null) {
            if (mIsPlaying) {
                mBtnPlay.setIconResource(R.drawable.play);
                mMusicLab.pauseMusic();
                mMusicLab.setPlaying(false);
                mIsPlaying = false;

            } else {
                mBtnPlay.setIconResource(R.drawable.pause);
                try {

                    mMusicLab.playMusic(mCurrentMusic, checkPlayCount());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMusicLab.setPlaying(true);
                mIsPlaying = true;


            }
        }
    }

    private boolean checkPlayCount() {
        int count = ++mPlayClickedCount;
        mMusicLab.setPlayClickedCount(count);
        mPlayClickedCount = count;
        return mPlayClickedCount != 1;

    }


    private void fillMusicBar() {

        mTxtTitle.setText(mCurrentMusic.getTittle());
        mTxtArtist.setText(mCurrentMusic.getArtist());
        MusicLab.getInstance(getActivity()).updateMusicImage(mImgMusic, mCurrentMusic.getImageUri(), imgWidth, imgHeight);

    }


    public interface CallBacks {
        void goToMusicPage();

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
                        mMusicLab.setCurrentPosition(getAdapterPosition());
                        mMusicPosition = getAdapterPosition();
                        mCurrentMusic = mMusicList.get(mMusicPosition);
                        mMusicLab.setCurrentMusic(mCurrentMusic);


                        if (mCurrentMusic != previousMusic) {
                            mMusicLab.setPlayClickedCount(0);
                            mMusicLab.setPlaying(false);

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
            mMusicLab.updateMusicImage(imgMusic, music.getImageUri(), imgWidth, imgHeight);


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
