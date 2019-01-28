package alireza.example.com.musicplayer.controllers.fragments;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicViewFragment extends Fragment implements FragmentStart, View.OnClickListener {


    //Widgets fields
    private MaterialButton BtnPlay;
    private MaterialButton BtnNext;
    private MaterialButton BtnPrivious;
    private MaterialButton BtnPlayState;
    private ViewPager PagerMusicPicture;
    private PagerAdapter mPagerAdapter;
    private AppCompatSeekBar mSeekBar;

    //Simple fields
    private int mMusicPostion;
    private Music mCurrentMusic;
    private List<Music> mMusicList;
    private MusicLab mMusicLab;

    private int mPlayClickedCount;
    private int mImgWidth = 250;
    private int mImgHeight = 250;
    private int mPreviousPosition;
    private int mMusicCurrentTime;
    private Handler mHandler;
    private int mediaPos;
    private MediaPlayer mMediaPlayer;
    private int mediaMax;
    private int mPreviousState;
    private int mPlayStateCounter;
    private Music.PlayState mPlayState = Music.PlayState.REPEAT_ALL;
    private Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            if (mMusicLab.getMediaPlayer().isPlaying()) {

                int mediaPos_new = mMusicLab.getMediaPlayer().getCurrentPosition();
                int mediaMax_new = mMusicLab.getMediaPlayer().getDuration();
                mSeekBar.removeCallbacks(moveSeekBarThread);
                mSeekBar.setMax(mediaMax_new);
                mSeekBar.setProgress(mediaPos_new);

                mHandler.postDelayed(this, 100); //Looping the thread after 0.1 second
            }

        }
    };

    public MusicViewFragment() {
        // Required empty public constructor
    }

    public static androidx.fragment.app.Fragment newInstance() {

        Bundle args = new Bundle();
        MusicViewFragment fragment = new MusicViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFieldValues();


    }

    private void setFieldValues() {
        if (getArguments() != null) {

            mMusicLab = MusicLab.getInstance(getActivity());
            mMusicPostion = mMusicLab.getCurrentPosition();
            mMusicList = mMusicLab.getMusicList();
            mCurrentMusic = mMusicLab.getCurrentMusic();
            mPlayClickedCount = 1;
            mPreviousPosition = mMusicPostion;
            mMusicCurrentTime = mMusicLab.getMediaPlayer().getCurrentPosition();
            mHandler = new Handler();
            mMediaPlayer = mMusicLab.getMediaPlayer();
            mPlayState = Music.PlayState.REPEAT_ALL;


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_view, container, false);
        initialization(view);
        setListeners();
        syncSeekBar();
        return view;
    }

    private void syncSeekBar() {
        mediaPos = mMediaPlayer.getCurrentPosition();
        mediaMax = mMediaPlayer.getDuration();

        mSeekBar.setMax(mediaMax); // Set the Maximum range of the
        mSeekBar.setProgress(mediaPos);// set current progress to song's
        mHandler.removeCallbacks(moveSeekBarThread);
        mHandler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds
    }

    @Override
    public void initialization(View view) {

        BtnPlay = view.findViewById(R.id.music_play_musicViewFragment);
        BtnNext = view.findViewById(R.id.music_next_musicViewFragment);
        BtnPrivious = view.findViewById(R.id.music_privious_musicViewFragment);
        BtnPlayState = view.findViewById(R.id.music_playState_musicViewFragment);
        PagerMusicPicture = view.findViewById(R.id.musicImage_container_musicViewFragment);
        mSeekBar = view.findViewById(R.id.music_seek_musicViewFragment);

        mCurrentMusic = mMusicLab.getCurrentMusic();

    }

    @Override
    public void setListeners() {
        BtnPlay.setOnClickListener(this);
        BtnNext.setOnClickListener(this);
        BtnPlayState.setOnClickListener(this);
        BtnPrivious.setOnClickListener(this);

        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mMusicList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {


                LayoutInflater inflater = LayoutInflater.from(getActivity());
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.music_pager, container, false);
                ImageView musicPicture = layout.findViewById(R.id.music_image_pager);
                TextView musicTitle = layout.findViewById(R.id.music_title_pager);
                musicTitle.setText(mMusicList.get(position).getTittle());
                mMusicLab.updateMusicImage(musicPicture, mMusicList.get(position).getImageUri(), mImgWidth, mImgHeight);
                container.addView(layout);


                mPlayClickedCount = 0;
                BtnPlay.setIconResource(R.drawable.pause);
                return layout;
            }


            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }


            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mMusicList.get(position).getTittle();
            }
        };
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser)
                    mMediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PagerMusicPicture.setAdapter(mPagerAdapter);

        PagerMusicPicture.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                checkMusicPosition();

                if (mMediaPlayer.getCurrentPosition() > 100) {
                    if (mPreviousPosition > position) {
                        moveToPrevious();
                        resetDatas(position);
                    } else if (mPreviousPosition < position) {
                        moveToNext();
                        resetDatas(position);

                    }


                }

            }

            @Override
            public void onPageSelected(int position) {


            }


            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

        PagerMusicPicture.setCurrentItem(mMusicPostion, true);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                moveToNext();
                PagerMusicPicture.setCurrentItem(mMusicPostion);
            }
        });

    }

    private void resetDatas(int position) {
        mCurrentMusic = mMusicLab.getCurrentMusic();


        mPlayClickedCount = 0;
        mPreviousPosition = position;
    }

    private void checkMusicPosition() {

        if (mMusicPostion == 0)
            BtnPrivious.setEnabled(false);
        else if (mMusicPostion == mMusicList.size() - 1)
            BtnNext.setEnabled(false);
        else {
            BtnNext.setEnabled(true);
            BtnPrivious.setEnabled(true);
        }
    }


    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.music_play_musicViewFragment:
                playOrPause();
                break;

            case R.id.music_next_musicViewFragment:
                moveToNext();
                break;
            case R.id.music_privious_musicViewFragment:
                moveToPrevious();
                break;
            case R.id.music_playState_musicViewFragment:
                checkPlayStateCount();
                break;
        }
    }


    private void checkPlayStateCount() {

        mPlayStateCounter++;

        switch (mPlayStateCounter) {
            case 0:
                mPlayState = Music.PlayState.REPEAT_ALL;

                BtnPlayState.setIconResource(R.drawable.replay);
                break;
            case 1:
                mPlayState = Music.PlayState.REPEAT_ONE;
                BtnPlayState.setIconResource(R.drawable.replay__once);
                break;
            case 2:
                mPlayState = Music.PlayState.SHUFFLE;
                mPlayStateCounter = -1;
                BtnPlayState.setIconResource(R.drawable.shuffle);
                break;


        }


    }

    private void playOrPause() {

        if (mMediaPlayer.isPlaying()) {
            BtnPlay.setIconResource(R.drawable.play);
            mMusicLab.pauseMusic();

        } else {
            BtnPlay.setIconResource(R.drawable.pause);
            try {

                mMusicLab.playMusic(mCurrentMusic, true);
            } catch (IOException e) {
                e.printStackTrace();
            }



        }

    }
/*
    private boolean checkPlayCount() {

        mPlayClickedCount++;
        return mPlayClickedCount != 1;

    }*/


    private void moveToNext() {


        mMusicLab.moveToNextMusic(mPlayState);
        mMusicPostion = mMusicLab.getCurrentPosition();


        mCurrentMusic = mMusicLab.getCurrentMusic();
        PagerMusicPicture.setCurrentItem(mMusicPostion, false);

        syncSeekBar();

    }

    private void moveToPrevious() {


        mMusicLab.moveToPreviousMusic(mPlayState);
        mMusicPostion = mMusicLab.getCurrentPosition();

        mCurrentMusic = mMusicLab.getCurrentMusic();
        PagerMusicPicture.setCurrentItem(mMusicPostion, false);

        syncSeekBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMusicLab.setCurrentMusic(mCurrentMusic);
        mMusicLab.setCurrentPosition(mMusicPostion);
        mMusicLab.setPlayClickedCount(mPlayClickedCount);
    }
}


