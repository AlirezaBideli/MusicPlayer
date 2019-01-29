package alireza.example.com.musicplayer.controllers.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import alireza.example.com.musicplayer.R;
import alireza.example.com.musicplayer.helpers.FragmentStart;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetFragment extends BottomSheetDialogFragment  implements FragmentStart, View.OnClickListener {

    private MaterialButton mBtnClose;

    public static BottomSheetFragment newInstance() {

        Bundle args = new Bundle();

        BottomSheetFragment fragment = new BottomSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //Set the custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_menu_button, null);
        initialization(view);
        setListeners();
        dialog.setContentView(view);

    }

    @Override
    public void initialization(View view) {
        mBtnClose=view.findViewById(R.id.menu_close);
    }

    @Override
    public void setListeners() {
    mBtnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.menu_close:
                dismiss();
                break;
        }
    }
}