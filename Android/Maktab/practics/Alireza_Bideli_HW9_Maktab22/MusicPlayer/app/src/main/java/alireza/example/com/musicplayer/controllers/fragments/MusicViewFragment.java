package alireza.example.com.musicplayer.controllers.fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alireza.example.com.musicplayer.R;
import alireza.example.com.musicplayer.helpers.FragmentStart;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicViewFragment extends Fragment implements FragmentStart {


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_music_view, container, false);
        initialization(view);
        setListeners();
        return  view;
    }

    @Override
    public void initialization(View view) {

    }

    @Override
    public void setListeners() {

    }
}
