package com.game.view;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.game.R;
import com.game.controller.GameModel;
import com.game.controller.SoundManager;
import com.game.controller.StoryManager;
import com.game.model.Spell;
import com.game.model.Weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameScreen extends AppCompatActivity {
    public ImageView image;
    public ConstraintLayout baseLayout;
    public TableLayout tableLayout;
    public TextView textView, hpLabel, armorLabel;
    public Button choice1, choice2, choice3, choice4;
    public Spinner weaponSpinner, spellSpinner;
    StoryManager storyManager;
    GameModel gameModel;
    public SoundManager soundManager;
    public final Handler textingHandler = new Handler();
    public final Handler handler = new Handler();
    public final Random rand = new Random();
    private boolean finishTexting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        gameModel = (GameModel) getIntent().getSerializableExtra("gameModel");
        soundManager = new SoundManager(this);
        storyManager = new StoryManager(this, gameModel);

        baseLayout = findViewById(R.id.baseLayout);
        tableLayout = findViewById(R.id.tableLayout);

        hpLabel = findViewById(R.id.hpLabel);
        armorLabel = findViewById(R.id.armorLabel);
        weaponSpinner = findViewById(R.id.weaponSpinner);
        spellSpinner = findViewById(R.id.spellSpinner);

        image = findViewById(R.id.gameImageView);
        textView = findViewById(R.id.gameTextView);
        choice1 = findViewById(R.id.choiceButton1);
        choice2 = findViewById(R.id.choiceButton2);
        choice3 = findViewById(R.id.choiceButton3);
        choice4 = findViewById(R.id.choiceButton4);
        spellSpinner.setVisibility(View.VISIBLE);
        darkUI();
        storyManager.opening();
    }

    public void restartGame() {
        Intent intent = new Intent(this, TitleScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void quitGame(){
        this.finishAffinity();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit");
        builder.setMessage("Are you sure you want to exit the game?");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clickButton1(View view) {
        soundManager.click();
        storyManager.selectPosition(storyManager.nextPosition1);
    }

    public void clickButton2(View view) {
        soundManager.click();
        storyManager.selectPosition(storyManager.nextPosition2);
    }

    public void clickButton3(View view) {
        soundManager.click();
        storyManager.selectPosition(storyManager.nextPosition3);
    }

    public void clickButton4(View view) {
        soundManager.click();
        storyManager.selectPosition(storyManager.nextPosition4);
    }

    public void timeLoop() {
        gameModel.setup(true);
        darkUI();
    }

    public void darkUI() {
        tableLayout.setVisibility(View.INVISIBLE);
        baseLayout.setBackgroundColor(Color.parseColor("#000000"));
        textView.setTextColor(Color.parseColor("#FFFFFF"));
    }

    public void lightUI() {
        tableLayout.setVisibility(View.INVISIBLE);
        baseLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        textView.setTextColor(Color.parseColor("#000000"));
    }

    public void startGameUI() {
        textView.setTextColor(Color.parseColor("#000000"));
        baseLayout.setBackgroundColor(Color.parseColor("#fefec8"));
        tableLayout.setVisibility(View.VISIBLE);
        soundManager.stopAllSoundEffect();
        soundManager.playBackGroundMusic();
        updatePlayerHp(0);
        obtainWeapon(null);
    }

    public boolean updatePlayerHp(int hpAmount) {
        if (hpAmount <= 0)
            gameModel.player.loseHP(-hpAmount);
        else {
            soundManager.healthUp();
            gameModel.player.restoreHP(hpAmount);
        }
        hpLabel.setText("HP: " + gameModel.player.getPlayerHP() + "/" + gameModel.player.getPlayerMaxHP());
        if (gameModel.player.getPlayerHP() == 0) {
            setChoicesAndNextPositions(new String[]{"Continue", "", "", "", "deadScreen", "", "", ""});
            return false;
        }
        return true;
    }

    public void updateSpellStatus() {
        if (spellSpinner.getVisibility() == View.INVISIBLE)
            spellSpinner.setVisibility(View.VISIBLE);
        int position = spellSpinner.getSelectedItemPosition();
        if (!gameModel.player.getSpellList().isEmpty()) {
            List<String> spellList = new ArrayList<>();
            for (int i = 0; i < gameModel.player.getSpellList().size(); i++) {
                Spell spell = gameModel.player.getSpellList().get(i);
                if (spell.getCoolDownRemain() == 0)
                    spellList.add(spell.getName() + " (Ready)");
                else spellList.add(spell.getName() + " (CD: " + spell.getCoolDownRemain() + ")");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.spinner_item, spellList);
            adapter.setDropDownViewResource(R.layout.spinner_item);
            spellSpinner.setAdapter(adapter);
            spellSpinner.setSelection(position);
        }
    }

    public void obtainWeapon(Weapon weaponObtain) {
        if (weaponObtain != null) {
            soundManager.obtainWeapon();
            gameModel.player.addWeapon(weaponObtain);
            Toast.makeText(getApplicationContext(), "You obtained the " + weaponObtain.getName() + "!!!", Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, gameModel.player.getWeaponList().stream().map(weapon -> weapon.getName()).collect(Collectors.toList()));
        adapter.setDropDownViewResource(R.layout.spinner_item);
        weaponSpinner.setAdapter(adapter);
        weaponSpinner.setSelection(gameModel.player.getWeaponList().size() - 1);
    }

    public void setChoicesAndNextPositions(String[] choicesAndNextPositions) {
        choice1.setText(choicesAndNextPositions[0]);
        storyManager.nextPosition1 = choicesAndNextPositions[4];
        choice2.setText(choicesAndNextPositions[1]);
        storyManager.nextPosition2 = choicesAndNextPositions[5];
        choice3.setText(choicesAndNextPositions[2]);
        storyManager.nextPosition3 = choicesAndNextPositions[6];
        choice4.setText(choicesAndNextPositions[3]);
        storyManager.nextPosition4 = choicesAndNextPositions[7];
        setChoiceVisibility();
    }

    public void setChoice1(String textChoice, String nextPosition) {
        choice1.setText(textChoice);
        storyManager.nextPosition1 = nextPosition;
        if (textChoice.equals("")) {
            choice1.setVisibility(View.INVISIBLE);
        } else choice1.setVisibility(View.VISIBLE);
    }

    public void setChoice2(String textChoice, String nextPosition) {
        choice2.setText(textChoice);
        storyManager.nextPosition2 = nextPosition;
        if (textChoice.equals("")) {
            choice2.setVisibility(View.INVISIBLE);
        } else
            choice2.setVisibility(View.VISIBLE);
    }

    public void setChoice3(String textChoice, String nextPosition) {
        choice3.setText(textChoice);
        storyManager.nextPosition3 = nextPosition;
        if (textChoice.equals("")) {
            choice3.setVisibility(View.INVISIBLE);
        } else choice3.setVisibility(View.VISIBLE);
    }

    public void setChoice4(String textChoice, String nextPosition) {
        choice4.setText(textChoice);
        storyManager.nextPosition4 = nextPosition;
        if (textChoice.equals("")) {
            choice4.setVisibility(View.INVISIBLE);
        } else choice4.setVisibility(View.VISIBLE);
    }

    public void setChoiceVisibility() {
        if (choice1.getText().equals(""))
            choice1.setVisibility(View.INVISIBLE);
        else
            choice1.setVisibility(View.VISIBLE);
        if (choice2.getText().equals(""))
            choice2.setVisibility(View.INVISIBLE);
        else
            choice2.setVisibility(View.VISIBLE);
        if (choice3.getText().equals(""))
            choice3.setVisibility(View.INVISIBLE);
        else
            choice3.setVisibility(View.VISIBLE);
        if (choice4.getText().equals(""))
            choice4.setVisibility(View.INVISIBLE);
        else
            choice4.setVisibility(View.VISIBLE);
    }

    public void displayText(String text) {
        textingHandler.removeCallbacksAndMessages(null);
        textView.setText(text);
    }

    public void displayTextSlowly(String text) {
        textingHandler.removeCallbacksAndMessages(null);
        textView.setText("");
        finishTexting = false;
        textingHandler.postDelayed(new Runnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (finishTexting) {
                    textView.setText(text);
                    finishTexting = false;
                } else if (currentIndex < text.length()) {
                    String currentChar = Character.toString(text.charAt(currentIndex));
                    textView.append(currentChar);
                    currentIndex++;
                    textingHandler.postDelayed(this, 5);
                }
            }
        }, 5);
    }

    public void finishTexting(View view) {
        finishTexting = true;
    }

    public void continueTextSlowly(String text) {
        finishTexting = true;
        String previousText = textView.getText().toString();

        textingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishTexting = false;
                textView.append("\n");
            }
        },100);

        textingHandler.postDelayed(new Runnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (finishTexting) {
                    textView.setText(previousText + "\n" + text);
                    finishTexting = false;
                } else if (currentIndex < text.length()) {
                    String currentChar = Character.toString(text.charAt(currentIndex));
                    textView.append(currentChar);
                    currentIndex++;
                    textingHandler.postDelayed(this, 5);
                }
            }
        }, 200);
    }
}