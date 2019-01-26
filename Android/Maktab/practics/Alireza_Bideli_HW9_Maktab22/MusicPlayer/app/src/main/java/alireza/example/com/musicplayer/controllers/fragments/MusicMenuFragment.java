package alireza.example.com.musicplayer.controllers.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alireza.example.com.musicplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicMenuFragment extends Fragment {


    public MusicMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_menu, container, false);
    }

}
