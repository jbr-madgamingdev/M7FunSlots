package com.masala7.devjo.funslots.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masala7.devjo.funslots.R;
import com.masala7.devjo.funslots.base.GameManager;
import com.masala7.devjo.funslots.base.GameMechanics;

import java.util.Objects;

public class SlotGame extends Activity {

    private static final String FIRST_RUN = "firstRun";
    private static final String SET_MUSIC = "music";
    private static final String SET_SOUND = "sound";
    private static final int COMBO_NUMBER = 7;
    private static final int CO_EFFICIENT = 72;
    private static final int CO_EFFICIENT_W = 142;
    private static final int CO_EFFICIENT_E = 212;
    private int position1 = 5;
    private int position2 = 5;
    private int position3 = 5;
    private final int[] slot = {1, 2, 3, 4, 5, 6, 7};

    private RecyclerView rv1;
    private RecyclerView rv2;
    private RecyclerView rv3;
    private GameManager layoutManager1;
    private GameManager layoutManager2;
    private GameManager layoutManager3;


    private TextView energyBallPrice;
    private TextView myPower;
    private TextView bet;
    private boolean firstRun;
    private GameMechanics gameLogic;
    private SharedPreferences pref;
    private MediaPlayer win;
    private MediaPlayer backgroundSound;
    public static final String PREFS_NAME = "FirstRun";


    private int playmusic;
    private int playsound;
    private ImageView setMusicOff;
    private ImageView setMusicOn;
    private ImageView setSoundOn;
    private ImageView setSoundOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_slot_game);

        ImageButton minusButton;
        ImageButton plusButton;
        SpinnerAdapter adapter;
        ImageView settingsButton;
        ImageButton spinButton;
        MediaPlayer mediaPlayer;

        backgroundSound = MediaPlayer.create(this,R.raw.bg_music);
        backgroundSound.setLooping(true);
        mediaPlayer = MediaPlayer.create(this, R.raw.spin);
        win = MediaPlayer.create(this, R.raw.win);

        pref = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firstRun = pref.getBoolean(FIRST_RUN, true);

        if (firstRun) {
            playmusic = 1;
            playsound = 1;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(FIRST_RUN, false);
            editor.apply();
        } else {
            playmusic= pref.getInt(SET_MUSIC, 1);
            playsound = pref.getInt(SET_SOUND, 1);
            checkMusic();

        }

        //Initializations
        gameLogic = new GameMechanics();
        settingsButton = findViewById(R.id.settings);
        spinButton = findViewById(R.id.spinButton);
        plusButton = findViewById(R.id.plusButton);
        minusButton = findViewById(R.id.minusButton);
        energyBallPrice = findViewById(R.id.energyBall);
        myPower = findViewById(R.id.energy);
        bet = findViewById(R.id.bet);
        adapter = new SpinnerAdapter();

        //RecyclerView settings
        rv1 = findViewById(R.id.spinner1);
        rv2 = findViewById(R.id.spinner2);
        rv3 = findViewById(R.id.spinner3);
        rv1.setHasFixedSize(true);
        rv2.setHasFixedSize(true);
        rv3.setHasFixedSize(true);

        layoutManager1 = new GameManager(this);
        layoutManager1.setScrollEnabled(false);
        rv1.setLayoutManager(layoutManager1);
        layoutManager2 = new GameManager(this);
        layoutManager2.setScrollEnabled(false);
        rv2.setLayoutManager(layoutManager2);
        layoutManager3 = new GameManager(this);
        layoutManager3.setScrollEnabled(false);
        rv3.setLayoutManager(layoutManager3);

        rv1.setAdapter(adapter);
        rv2.setAdapter(adapter);
        rv3.setAdapter(adapter);
        rv1.scrollToPosition(position1);
        rv2.scrollToPosition(position2);
        rv3.scrollToPosition(position3);

        setText();
        updateText();

        //RecyclerView listeners
        rv1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv1.scrollToPosition(gameLogic.getPosition(0));
                    layoutManager1.setScrollEnabled(false);
                }
            }
        });

        rv2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv2.scrollToPosition(gameLogic.getPosition(1));
                    layoutManager2.setScrollEnabled(false);
                }
            }
        });

        rv3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv3.scrollToPosition(gameLogic.getPosition(2));
                    layoutManager3.setScrollEnabled(false);
                    updateText();
                    if (gameLogic.getHasWon()) {
                        if (playsound == 1) {
                            win.start();
                        }
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.win_splash,findViewById(R.id.win_splash));
                        TextView winCoins = layout.findViewById(R.id.win_coins);
                        winCoins.setText(gameLogic.getPrize());
                        Toast toast = new Toast(SlotGame.this);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setView(layout);
                        toast.show();
                        gameLogic.setHasWon(false);
                    }
                }
            }
        });

        //Button listeners
        spinButton.setOnClickListener(v -> {
            if(playsound == 1){
                mediaPlayer.start();
            }
            layoutManager1.setScrollEnabled(true);
            layoutManager2.setScrollEnabled(true);
            layoutManager3.setScrollEnabled(true);
            gameLogic.getSpinResults();
            position1 = gameLogic.getPosition(0) + CO_EFFICIENT;
            position2 = gameLogic.getPosition(1) + CO_EFFICIENT_W;
            position3 = gameLogic.getPosition(2) + CO_EFFICIENT_E;
            rv1.smoothScrollToPosition(position1);
            rv2.smoothScrollToPosition(position2);
            rv3.smoothScrollToPosition(position3);
        });

        plusButton.setOnClickListener(v -> {
            gameLogic.betUp();
            updateText();
        });

        minusButton.setOnClickListener(v -> {
            gameLogic.betDown();
            updateText();
        });

        settingsButton.setOnClickListener(v -> showSettingsDialog());
    }

    private void setText(){
        if(firstRun){
            gameLogic.setMyCoins(1000);
            gameLogic.setBet(5);
            gameLogic.setJackpot(100000);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(FIRST_RUN, false);
            editor.apply();

        }else {
            String coins = pref.getString("coins","");
            String bet = pref.getString("bet","");
            String jackpot = pref.getString("jackpot","");
            int myCoinsVal = Integer.parseInt(coins);
            int myBetVal = Integer.parseInt(bet);
            int jackPotVal = Integer.parseInt(jackpot);
            gameLogic.setMyCoins(myCoinsVal);
            gameLogic.setBet(myBetVal);
            gameLogic.setJackpot(jackPotVal);
        }
    }

    private void updateText() {
        energyBallPrice.setText(gameLogic.getJackpot());
        myPower.setText(gameLogic.getMyCoins());
        bet.setText(gameLogic.getBet());

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("coins",gameLogic.getMyCoins());
        editor.putString("bet",gameLogic.getBet());
        editor.putString("jackpot",gameLogic.getJackpot());
        editor.apply();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(SlotGame.this);
            View view = layoutInflater.inflate(R.layout.spinner_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % COMBO_NUMBER;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.combination_1);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.combination_2);
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.combination_3);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.combination_4);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.combination_5);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.combination_6);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.combination_7);
                    break;
                default:
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    private void showSettingsDialog() {
        final Dialog dialog;

        dialog = new Dialog(this, R.style.WinDialog);
        Objects.requireNonNull(dialog.getWindow()).setContentView(R.layout.settings);

        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss()); // Close the dialog when the close button is clicked

        setMusicOn = dialog.findViewById(R.id.music_on);
        setMusicOff =  dialog.findViewById(R.id.music_off);
        setSoundOn = dialog.findViewById(R.id.sounds_on);
        setSoundOff = dialog.findViewById(R.id.sounds_off);

        setMusicOn.setOnClickListener(v -> {
            playmusic = 0;
            checkMusic();
            setMusicOn.setVisibility(View.INVISIBLE);
            setMusicOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(SET_MUSIC, playmusic);
            editor.apply();
        });

        setMusicOn.setOnClickListener(v -> {
            playmusic = 1;
            backgroundSound.start();
            recreate();
            dialog.show();
            setMusicOn.setVisibility(View.VISIBLE);
            setMusicOff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(SET_MUSIC, playmusic);
            editor.apply();
        });

        setSoundOn.setOnClickListener(v -> {
            playsound = 0;
            setSoundOn.setVisibility(View.INVISIBLE);
            setSoundOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(SET_SOUND, playsound);
            editor.apply();
        });


        setSoundOff.setOnClickListener(v -> {
            playsound = 1;
            recreate();
            dialog.show();
            setSoundOn.setVisibility(View.INVISIBLE);
            setSoundOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(SET_SOUND, playsound);
            editor.apply();
        });

        checkMusicDraw();
        checkSoundDraw();

        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        backgroundSound.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkMusic();
    }

    private void checkMusic(){
        if (playmusic == 1){
            backgroundSound.start();
        }
        else {
            backgroundSound.pause();
        }
    }

    private void checkMusicDraw(){
        if (playmusic == 1){
            setMusicOn.setVisibility(View.VISIBLE);
            setMusicOff.setVisibility(View.INVISIBLE);
        }
        else {
            setMusicOn.setVisibility(View.INVISIBLE);
            setMusicOff.setVisibility(View.VISIBLE);
        }
    }

    private void checkSoundDraw(){
        if (playsound == 1){
            setSoundOn.setVisibility(View.VISIBLE);
            setSoundOff.setVisibility(View.INVISIBLE);
        }
        else {
            setSoundOn.setVisibility(View.INVISIBLE);
            setSoundOff.setVisibility(View.VISIBLE);
        }
    }
}