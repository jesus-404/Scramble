package com.example.scramble;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.Objects;
import java.util.Random;

public class HomeActivity extends MainActivity {

    final int MIN_COL = 3;
    final int MAX_ROW = 5;
    String randomWord = getRandomWord();
    int correctTiles = 0;
    int wordPos = 0;
    int rowPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Play Animation after fade
        playAnimation();
        // Fill play area (GridLayout)
        populateGridLayout(randomWord.length());
        // Fill keyboard
        populateKeyboard();
        // Check if playing for the first time
        if (isFirstTime()) {
            new Handler().postDelayed(this::openDialogBox, 150);
        }
        // If clicked on info button, display info dialog box
        LinearLayout openDialogBox = findViewById(R.id.info_button);
        openDialogBox.setOnClickListener(v -> openDialogBox());
        // If clicked on stats button, display stats dialog box
        LinearLayout openStatsBox = findViewById(R.id.stats_button);
        openStatsBox.setOnClickListener(v -> openStatsBox());
    }

    public void restartGame() {
        //Animation animation;
        correctTiles = 0;
        wordPos = 0;
        rowPos = 0;
        randomWord = getRandomWord();

        GridLayout gridLayout = findViewById(R.id.play_area);
        if (gridLayout != null) {
            new Handler().postDelayed(() -> {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate_out_bottom);
                gridLayout.startAnimation(animation);
            }, 150);
            new Handler().postDelayed(() -> {
                depopulateGridLayout();
                populateGridLayout(randomWord.length());

                Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate_in_bottom);
                gridLayout.startAnimation(animation);
            }, 900);
        }
    }

    public String getRandomWord() {
        String[] words = {
                "nap", "cat", "mop", "palm", "cloth", "apple", "stone", "glove", "paper",
                 "brick", "plumb", "pearl", "bark", "wave", "fish", "wood", "sand"
        };

        Random random = new Random();
        int randomIndex = random.nextInt(words.length);
        return words[randomIndex];
    }

    private void playAnimation() {
        Animation animation;

        TextView textView = findViewById(R.id.app_name);
        if (textView != null) {
            animation = AnimationUtils.loadAnimation(this, R.anim.translate_in_top);
            textView.startAnimation(animation);
        }

        LinearLayout linearLayout = findViewById(R.id.header_buttons);
        if (linearLayout != null) {
            animation = AnimationUtils.loadAnimation(this, R.anim.translate_in_right);
            linearLayout.startAnimation(animation);
        }

        GridLayout gridLayout = findViewById(R.id.play_area);
        if (gridLayout != null) {
            animation = AnimationUtils.loadAnimation(this, R.anim.translate_in_bottom);
            gridLayout.startAnimation(animation);
        }

        RelativeLayout relativeLayout = findViewById(R.id.layout_keyboard);
        if (relativeLayout != null) {
            animation = AnimationUtils.loadAnimation(this, R.anim.translate_in_bottom2);
            relativeLayout.startAnimation(animation);
        }
    }

    private void populateGridLayout(int columnCount) {
        GridLayout gridLayout = findViewById(R.id.play_area);
        gridLayout.setColumnCount(columnCount);
        gridLayout.setRowCount(MAX_ROW);

        for (int i = 0; i < (columnCount * MAX_ROW); i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tileView = inflater.inflate(R.layout.tile_default, gridLayout, false);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;

            tileView.setLayoutParams(params);
            gridLayout.addView(tileView);
        }
    }

    private void depopulateGridLayout() {
        GridLayout gridLayout = findViewById(R.id.play_area);
        gridLayout.removeAllViews(); // Remove all child views
    }

    private void populateKeyboard() {
        String[] qwertyKeyboard = {
                "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L",
                "Z", "X", "C", "V", "B", "N", "M"
        };

        GridLayout gridLayout = findViewById(R.id.keyboard_buttons);
        gridLayout.setColumnCount(9);

        for (String key : qwertyKeyboard) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tileView = inflater.inflate(R.layout.layout_keyboard_button, gridLayout, false);
            TextView textView = tileView.findViewById(R.id.button_key);
            textView.setText(key);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            tileView.setLayoutParams(params);

            textView.setOnClickListener(v -> {
                handleClick(key);
            });

            gridLayout.addView(tileView);
        }
    }

    private void handleClick(String key) {
        GridLayout gridLayout = findViewById(R.id.play_area);
        int wordLength = randomWord.length();


        View tile = gridLayout.getChildAt(wordPos);
        TextView tileText = tile.findViewById(R.id.title_text);
        tileText.setText(key);
        correctTiles += setTileColor(tile, key, wordLength);
        wordPos++;
        // Check if row is complete
        if (wordPos % wordLength == 0) {
            rowPos++;
            if (correctTiles == wordLength) {
                // Win condition
                openWinDialogBox();
                return;
            } else if (rowPos == MAX_ROW) {
                // Lose condition
                openFailDialogBox();
                return;
            } else {
                correctTiles = 0;
            }
        }
    }

    private int setTileColor(View tile, String key, int wordLength) {
        int correctTile = 0;
        RelativeLayout solidLayout = tile.findViewById(R.id.tile_solid);
        RelativeLayout strokeLayout = tile.findViewById(R.id.tile_stroke);

        // Set default background tint
        solidLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_tertiary));
        strokeLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_quinary));
        // Check if the letter matches the correct position
        if (randomWord.charAt(wordPos % wordLength) == Character.toLowerCase(key.charAt(0))) {
            solidLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_success1));
            strokeLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_success2));
            correctTile = 1;
        } else {
            boolean isInWord = false;
            for (int i = 0; i < wordLength; i++) {
                if (randomWord.charAt(i) == Character.toLowerCase(key.charAt(0))) {
                    isInWord = true;
                    break;
                }
            }
            if (isInWord) {
                // If  letter exists somewhere in the word
                solidLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_warning1));
                strokeLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_warning2));
            }
        }
        return correctTile;
    }

    private boolean isFirstTime() {
        String sharedPrefFile = "com.example.android.sharedprefs";
        String key = "isFirstTime";

        SharedPreferences preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(key, true);

        if (isFirstTime) {
            preferences.edit().putBoolean(key, false).apply();
        }
        return isFirstTime;
    }

    private void openDialogBox() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info, null);

        // Set tile letters
        String[] tile_ids = {"tile_fail", "tile_warning", "tile_success"};
        String[] tiles_text = {"A", "B", "C"};
        for (int i = 0; i < tile_ids.length; i++) {
            // Change tile text
            View includeLayout = dialogView.findViewById(getResources().getIdentifier(tile_ids[i], "id", getPackageName()));
            TextView titleText = includeLayout.findViewById(R.id.title_text);
            titleText.setText(tiles_text[i]);

            // Change tile color
            RelativeLayout solidLayout = includeLayout.findViewById(R.id.tile_solid);
            RelativeLayout strokeLayout = includeLayout.findViewById(R.id.tile_stroke);
            switch (tiles_text[i]) {
                case "A": {
                    solidLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_tertiary));
                    strokeLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_quinary));
                    break;
                }
                case "B": {
                    solidLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_warning1));
                    strokeLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_warning2));
                    break;
                }
                case "C": {
                    solidLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_success1));
                    strokeLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_success2));
                    break;
                }
                default: {
                    break;
                }
            }
        }

        // Build the dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        AlertDialog dialog = builder.create();

        // Handle close button
        LinearLayout closeDialogBox = dialogView.findViewById(R.id.closure_button);
        closeDialogBox.setOnClickListener(view -> dialog.dismiss());

        // Handle animations
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void openStatsBox() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_stats, null);

        // Set stats
        String sharedPrefFile = "com.example.android.sharedprefs";
        String keyWins = "num_wins";
        String keyLosses = "num_losses";
        String keyPlays = "num_plays";

        SharedPreferences preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        int numWins = preferences.getInt(keyWins, 0);
        int numLosses = preferences.getInt(keyLosses, 0);
        int numPlays = preferences.getInt(keyPlays, 0);

        TextView textView = dialogView.findViewById(R.id.num_wins);
        textView.setText(String.valueOf(numWins));
        textView = dialogView.findViewById(R.id.num_loss);
        textView.setText(String.valueOf(numLosses));
        textView = dialogView.findViewById(R.id.num_plays);
        textView.setText(String.valueOf(numPlays));

        // Build the dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        AlertDialog dialog = builder.create();

        // Handle close button
        LinearLayout closeDialogBox = dialogView.findViewById(R.id.closure_button);
        closeDialogBox.setOnClickListener(view -> dialog.dismiss());

        // Handle animations
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.FastDialogAnimation;
        dialog.show();
    }

    private void openFailDialogBox() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_lose, null);

        // Build the dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false);
        AlertDialog dialog = builder.create();

        // Handle close button
        LinearLayout closeDialogBox = dialogView.findViewById(R.id.restart_button);
        closeDialogBox.setOnClickListener(view -> {
            String sharedPrefFile = "com.example.android.sharedprefs";
            String keyLosses = "num_losses";
            String keyPlays = "num_plays";

            SharedPreferences preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
            int numLosses = preferences.getInt(keyLosses, 0);
            preferences.edit().putInt(keyLosses, (numLosses + 1)).apply();
            int numPlays = preferences.getInt(keyPlays, 0);
            preferences.edit().putInt(keyPlays, (numPlays + 1)).apply();

            restartGame();
            dialog.dismiss();
        });

        // Handle animations
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.FastDialogAnimation;
        dialog.show();
    }

    private void openWinDialogBox() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_win, null);

        // Show # of rows attempted to win
        TextView textView = dialogView.findViewById(R.id.win_stats);
        textView.setText("Congratulations! You've won in " + rowPos + " attempt(s)!");

        // Build the dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false);
        AlertDialog dialog = builder.create();

        // Handle close button
        LinearLayout closeDialogBox = dialogView.findViewById(R.id.restart_button);
        closeDialogBox.setOnClickListener(view -> {
            String sharedPrefFile = "com.example.android.sharedprefs";
            String keyWins = "num_wins";
            String keyPlays = "num_plays";

            SharedPreferences preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
            int numWins = preferences.getInt(keyWins, 0);
            preferences.edit().putInt(keyWins, (numWins + 1)).apply();
            int numPlays = preferences.getInt(keyPlays, 0);
            preferences.edit().putInt(keyPlays, (numPlays + 1)).apply();

            restartGame();
            dialog.dismiss();
        });

        // Handle animations
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.FastDialogAnimation;
        dialog.show();
    }
}