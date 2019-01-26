package alireza.example.com.musicplayer.models;

import java.util.ArrayList;
import java.util.List;

public class MusicLab {
    private static final MusicLab instance = new MusicLab();
    private List<Music> mMusicList = new ArrayList<>();

    private MusicLab() {
    }

    public static MusicLab getInstance() {
        return instance;
    }


    //Methods for Musics

    public void setMusicList(List<Music> music) {
        mMusicList = music;
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

    public void addMusic(Music music) {
        mMusicList.add(music);
    }


}
