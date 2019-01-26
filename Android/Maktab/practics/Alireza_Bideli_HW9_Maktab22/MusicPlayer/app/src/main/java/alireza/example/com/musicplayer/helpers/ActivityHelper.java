package alireza.example.com.musicplayer.helpers;

import android.content.Context;
import android.content.Intent;

import alireza.example.com.musicplayer.controllers.activities.MusicPalyerActivity;

/**
 * Created by ADMIN on 1/24/2019.
 */


//this class is used to defined all of the activity intents
//A means Activity
public class ActivityHelper {


    public static Intent MusicListAIntent(Context context) {
        Intent intent = new Intent(context, MusicPalyerActivity.class);
        return intent;
    }

}
