package com.game.controller;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.game.R;
import com.game.model.Effect;
import com.game.model.Monster;
import com.game.model.Spell;
import com.game.model.Weapon;
import com.game.model.monsters.Monster_DemonGeneral;
import com.game.model.monsters.Monster_Goblin;
import com.game.model.monsters.Monster_RiverMonster;
import com.game.model.monsters.Monster_ShadowSerpent;
import com.game.model.spells.Spell_PoisonBreeze;
import com.game.model.weapons.Weapon_Trident;
import com.game.view.GameScreen;

import java.util.Random;

public class CombatManager {
    StoryManager storyManager;
    GameModel gameModel;
    GameScreen ui;
    SoundManager soundManager;
    private Random rand;
    private boolean encounterMonsterTurn = true;

    public CombatManager(StoryManager storyManager) {
        this.storyManager = storyManager;
        this.gameModel = storyManager.gameModel;
        this.ui = storyManager.ui;
        this.soundManager = ui.soundManager;
        this.rand = ui.rand;
    }

    public void encounterGoblin() {
        if (gameModel.goblin == null)
            gameModel.goblin = new Monster_Goblin(gameModel.difficultRate);
        gameModel.position = "encounterGoblin";
        encounterMonster(gameModel.goblin);
    }

    public void encounterEvilWitch() {
        if (gameModel.poisonBreeze == null) {
            gameModel.poisonBreeze = new Spell_PoisonBreeze();
        }
        if (!gameModel.position.equalsIgnoreCase("encounterEvilWitch")) {
            gameModel.lastPosition = gameModel.position;
            ui.image.setImageResource(R.drawable.evil_witch);
            soundManager.evilWitch();
            soundManager.playBattleMusic();
            Toast.makeText(ui.getApplicationContext(), "The poison breeze slowly drains your health.", Toast.LENGTH_SHORT).show();
            gameModel.poisonBreeze.activeEffect();
            gameModel.player.addEffect(gameModel.poisonBreeze.getEffect());
        }
        gameModel.position = "encounterEvilWitch";

        encounterMonster(gameModel.evilWitch);
    }

    public void encounterRiverMonster() {
        if (gameModel.riverMonster == null)
            gameModel.riverMonster = new Monster_RiverMonster(gameModel.difficultRate);
        if (!gameModel.position.equalsIgnoreCase("encounterRiverMonster")) {
            gameModel.lastPosition = gameModel.position;
            Glide.with(ui).load(R.drawable.river_monster).into(ui.image);
            soundManager.playBattleMusic();
            soundManager.riverMonster();
            Toast.makeText(ui.getApplicationContext(), "You come face to face with a formidable river monster.", Toast.LENGTH_SHORT).show();
        }
        gameModel.position = "encounterRiverMonster";

        encounterMonster(gameModel.riverMonster);
    }

    public void encounterShadowSerpent() {
        if (gameModel.shadowSerpent == null)
            gameModel.shadowSerpent = new Monster_ShadowSerpent(gameModel.difficultRate);
        if (!gameModel.position.equalsIgnoreCase("encounterShadowSerpent")) {
            gameModel.lastPosition = gameModel.position;
            Glide.with(ui).load(R.drawable.shadow_serpent).into(ui.image);
            soundManager.playBattleMusic();
            soundManager.shadowSerpent();
            Toast.makeText(ui.getApplicationContext(), "You come face to face with a menacing shadow serpent.", Toast.LENGTH_SHORT).show();
        }
        gameModel.position = "encounterShadowSerpent";

        encounterMonster(gameModel.shadowSerpent);
    }

    public void encounterDemonGeneral() {
        if (gameModel.demonGeneral == null)
            gameModel.demonGeneral = new Monster_DemonGeneral(gameModel.difficultRate);
        if (!gameModel.position.equalsIgnoreCase("encounterDemonGeneral")) {
            gameModel.lastPosition = gameModel.position;
            Glide.with(ui).load(R.drawable.demon_general).into(ui.image);
            soundManager.playBattleMusic();
            soundManager.demonGeneral();
            Toast.makeText(ui.getApplicationContext(), "You confront the demon general, standing face-to-face with one of the mightiest warriors in the demon king's army.", Toast.LENGTH_LONG).show();
        }
        gameModel.position = "encounterDemonGeneral";

        encounterMonster(gameModel.demonGeneral);
    }

    public void attackGoblin(boolean useSpell) {
        if (gameModel.goblin.getMonsterCurrentHP() > 0) {
            attackMonster(gameModel.goblin, useSpell);
            encounterMonster(gameModel.goblin);
        } else {
            soundManager.playBackGroundMusic();
            Toast.makeText(ui.getApplicationContext(), "You have defeated the goblin!", Toast.LENGTH_SHORT).show();
            gameModel.isALiveGoblin = false;
            gameModel.goblin = null;
            storyManager.deeperInsideGoblinCave();
        }
    }

    public void attackEvilWitch(boolean useSpell) {
        if (gameModel.evilWitch.getMonsterCurrentHP() > 1) {
            attackMonster(gameModel.evilWitch, useSpell);
            encounterMonster(gameModel.evilWitch);
        } else {
            soundManager.playBackGroundMusic();
            gameModel.evilWitch.setMonsterCurrentHP(1);
            ui.displayTextSlowly(gameModel.evilWitch.getName() + "'s HP: " + gameModel.evilWitch.getMonsterCurrentHP() + "/" + gameModel.evilWitch.getMonsterMaxHP());
            Toast.makeText(ui.getApplicationContext(), "You have defeated the Evil Witch!", Toast.LENGTH_SHORT).show();

            for (Effect effect : gameModel.player.getEffectList()) {
                if (effect.getName().equalsIgnoreCase("Poisonous")) {
                    gameModel.player.removeEffect(effect);
                    break;
                }
            }

            gameModel.isDefeatedEvilWitch = true;
            gameModel.evilWitch = null;
            storyManager.talkWitch4();
        }
    }

    public void attackRiverMonster(boolean useSpell) {
        if (gameModel.riverMonster.getMonsterCurrentHP() > 0) {
            attackMonster(gameModel.riverMonster, useSpell);
            encounterMonster(gameModel.riverMonster);
        } else {
            soundManager.playBackGroundMusic();
            ui.displayTextSlowly(gameModel.riverMonster.getName() + "'s HP: " + gameModel.riverMonster.getMonsterCurrentHP() + "/" + gameModel.riverMonster.getMonsterMaxHP());
            Toast.makeText(ui.getApplicationContext(), "Victorious against the river monster, you claim a gleaming trident as your reward.", Toast.LENGTH_SHORT).show();

            if (gameModel.trident == null)
                gameModel.trident = new Weapon_Trident();
            ui.obtainWeapon(gameModel.trident);

            gameModel.isALiveRiverMonster = false;
            gameModel.riverMonster = null;
            storyManager.selectPosition(gameModel.lastPosition);
        }
    }

    public void attackShadowSerpent(boolean useSpell) {
        if (gameModel.shadowSerpent.getMonsterCurrentHP() > 0) {
            attackMonster(gameModel.shadowSerpent, useSpell);
            encounterMonster(gameModel.shadowSerpent);
        } else {
            soundManager.playBackGroundMusic();
            ui.displayTextSlowly(gameModel.shadowSerpent.getName() + "'s HP: " + gameModel.shadowSerpent.getMonsterCurrentHP() + "/" + gameModel.shadowSerpent.getMonsterMaxHP());
            Toast.makeText(ui.getApplicationContext(), "You have defeated the shadow serpent!", Toast.LENGTH_SHORT).show();

            gameModel.isAliveShadowSerpent = false;
            gameModel.shadowSerpent = null;
            storyManager.insideDemonHideout();
        }
    }

    public void attackDemonGeneral(boolean useSpell) {
        if (gameModel.demonGeneral.getMonsterCurrentHP() > 0) {
            attackMonster(gameModel.demonGeneral, useSpell);
            encounterMonster(gameModel.demonGeneral);
        } else {
            soundManager.playBackGroundMusic();
            gameModel.isAliveDemonGeneral = false;
            gameModel.demonGeneral = null;
            storyManager.defeatDemonGeneral();
        }
    }

    public void encounterMonster(Monster monster) {
        ui.setChoicesAndNextPositions("Fight", "Try to run", "", "", gameModel.lastPosition, "tryToRun", "", "");
        if (monster.equals(gameModel.goblin)) {
            storyManager.nextPosition1 = "attackGoblin";
            if (!gameModel.player.getSpellList().isEmpty()) {
                ui.updateSpellStatus();
                ui.setChoice2("Use spell", "attackGoblinWithSpell");
                ui.setChoice3("Try to run", "tryToRun");
            }
        } else if (monster.equals(gameModel.riverMonster)) {
            storyManager.nextPosition1 = "attackRiverMonster";
            if (!gameModel.player.getSpellList().isEmpty()) {
                ui.updateSpellStatus();
                ui.setChoice2("Use spell", "attackRiverMonsterWithSpell");
                ui.setChoice3("Try to run", "tryToRun");
            }
        } else if (monster.equals(gameModel.shadowSerpent)) {
            storyManager.nextPosition1 = "attackShadowSerpent";
            if (!gameModel.player.getSpellList().isEmpty()) {
                ui.updateSpellStatus();
                ui.setChoice2("Use spell", "attackShadowSerpentWithSpell");
                ui.setChoice3("Try to run", "tryToRun");
            }
        } else if (monster.equals(gameModel.demonGeneral)) {
            storyManager.nextPosition1 = "attackDemonGeneral";
            if (!gameModel.player.getSpellList().isEmpty()) {
                ui.updateSpellStatus();
                ui.setChoice2("Use spell", "attackDemonGeneralWithSpell");
                ui.setChoice3("Try to run", "tryToRun");
            }
        } else if (monster.equals(gameModel.evilWitch)) {
            storyManager.nextPosition1 = "attackEvilWitch";
            if (!gameModel.player.getSpellList().isEmpty()) {
                ui.updateSpellStatus();
                ui.setChoice2("Use spell", "attackEvilWitchWithSpell");
                ui.setChoice3("Try to run", "tryToRun");
            }
        }

        if (encounterMonsterTurn) {
            StringBuilder monsterStatus = new StringBuilder(monster.getName() + "'s HP: " + monster.getMonsterCurrentHP() + "/" + monster.getMonsterMaxHP());
            if (!monster.getEffectList().isEmpty())
                for (Effect effect : monster.getEffectList()) {
                    monsterStatus.append("\n" + monster.getName() + " is " + effect.getName());
                    if (effect.getRemain() > 1) {
                        monsterStatus.append(" (Remain: " + effect.getRemain() + ")");
                    }
                }
            ui.displayText(monsterStatus.toString()+"\n");
        } else {
            encounterMonsterTurn = true;
            ui.choice1.setText("Continue");
            ui.choice2.setText("");
            ui.choice3.setText("");

            if (ui.updatePlayerHp(0) && monster.getMonsterCurrentHP() > 0 || (gameModel.evilWitch != null && gameModel.evilWitch.getMonsterCurrentHP() > 1)) {
                storyManager.nextPosition1 = gameModel.position;
            }
        }
        ui.setChoiceVisibility();
    }

    public void attackMonster(Monster monster, boolean useSpell) {
        boolean monsterAbleToAttack = true;
        Spell spell = null;
        StringBuilder text = new StringBuilder("");
        //PLAYER ATTACK
        if (useSpell) {
            spell = gameModel.player.getSpellList().get(ui.spellSpinner.getSelectedItemPosition());
            if (spell.getCoolDownRemain() > 0) {
                Toast.makeText(ui.getApplicationContext(), spell.getName() + " is not ready!", Toast.LENGTH_SHORT).show();
                encounterMonster(monster);
                return;
            }

            text.append("\nYou cast " + spell.getName() + ". " + spell.getDescription());
            if (spell.getDamage() > 0) {
                monster.loseHP(spell.getDamage());
                text.append(" -> " + monster.getName() + "'s HP: " + monster.getMonsterCurrentHP() + "/" + monster.getMonsterMaxHP() + "\n");
            }
            if (spell.getEffect() != null) {
                spell.activeEffect();
                monster.addEffect(spell.getEffect());
            }
            if (spell.equals(gameModel.waterSurge)) {
                ui.updatePlayerHp(-spell.getDamage());
                monsterAbleToAttack = false;
            }
        } else {
            Weapon currentWeapon = gameModel.player.getWeaponList().get(ui.weaponSpinner.getSelectedItemPosition());
            int playerDamage = gameModel.player.getBaseAttack() + rand.nextInt(currentWeapon.getCriticalAttackDamage() - currentWeapon.getAttackDamage()) + currentWeapon.getAttackDamage();
            text.append("\nYou attacked with " + currentWeapon.getName() + " and gave " + playerDamage + " damage!");
            monster.loseHP(playerDamage);
            text.append(" -> " + monster.getMonsterCurrentHP() + "/" + monster.getMonsterMaxHP() + "\n");
        }

        if (!gameModel.player.getSpellList().isEmpty()) {
            for (Spell sp : gameModel.player.getSpellList()) {
                if (spell != null && sp.getName().equals(spell.getName()))
                    sp.resetCoolDown();
                else
                    sp.decreaseCoolDown();
            }
        }

        if (!monster.getEffectList().isEmpty())
            for (Effect effect : monster.getEffectList()) {
                text.append("\n" + effect.getDescriptionToMonster());

                if (effect.getDamage() > 0) {
                    monster.loseHP(effect.getDamage());
                    text.append(" -> " + monster.getMonsterCurrentHP() + "/" + monster.getMonsterMaxHP());
                }

                if (effect.getName().equalsIgnoreCase(gameModel.paralyzedEffect.getName()))
                    monsterAbleToAttack = false;

                if (spell == null || (spell != null && !spell.getEffect().getName().equals(effect.getName()))) {
                    effect.reduceRemain();
                    if (effect.getRemain() == 0)
                        monster.removeEffect(effect);
                }
                text.append("\n");
            }
        monsterAttackTurn(monster, monsterAbleToAttack, text);
    }

    private void monsterAttackTurn(Monster monster, boolean monsterAbleToAttack, StringBuilder text) {
        //MONSTER ATTACK
        if (!gameModel.player.getEffectList().isEmpty()) {
            for (Effect effect : gameModel.player.getEffectList()) {
                text.append("\n" + effect.getDescriptionToPlayer() + " (Remain: " + effect.getRemain() + ")");

                if (effect.getDamage() > 0) {
                    gameModel.player.loseHP((int) Math.floor(effect.getDamage() * gameModel.difficultRate));
                    text.append(" -> " + gameModel.player.getPlayerHP() + "/" + gameModel.player.getPlayerMaxHP());
                }
                effect.reduceRemain();
                if (effect.getRemain() == 0)
                    gameModel.player.removeEffect(effect);
            }
            text.append("\n");
        }

        int monsterDamage;
        if (monsterAbleToAttack) {
            monsterDamage = rand.nextInt(monster.getCriticalAttackDamage() - monster.getAttackDamage()) + monster.getAttackDamage() - (gameModel.player.getArmor() != null ? gameModel.player.getArmor().getDamageReduced() : 0);
            text.append("\nThe " + monster.getName() + " attacked you." + (gameModel.player.getArmor() != null ? (" With " + gameModel.player.getArmor().getName()) : "") + " you took " + (monsterDamage > 0 ? monsterDamage : 0) + " damage!");
            if (monsterDamage > 0) {
                gameModel.player.loseHP(monsterDamage);
                text.append(" -> " + gameModel.player.getPlayerHP() + "/" + gameModel.player.getPlayerMaxHP() + "\n");
            }
        }
        ui.continueDisplayText(text.toString());
        encounterMonsterTurn = false;
    }

    public void tryToRun() {
        if (gameModel.position.equalsIgnoreCase("encounterEvilWitch") || gameModel.position.equalsIgnoreCase("talkWitch3")) {
            ui.continueTextSlowly("Escape from the clutches of the witch proves futile as she blocks your path.");
            ui.setChoicesAndNextPositions("Continue", "", "", "", "encounterEvilWitch", "", "", "");
        } else if (rand.nextBoolean()) {
            soundManager.playBackGroundMusic();
            ui.continueTextSlowly("With a swift dodge, you evade the monster's attack and successfully escape, fleeing to safety.");
            ui.setChoicesAndNextPositions("Continue", "", "", "", gameModel.lastPosition, "", "", "");
        } else {
            int damageTaken = (int) Math.ceil(2 * gameModel.difficultRate) - (gameModel.player.getArmor() != null ? gameModel.player.getArmor().getDamageReduced() : 0);
            ui.continueTextSlowly("You were unable to evade the monster's pursuit and suffered a blow, losing " + (damageTaken > 0 ? damageTaken : 0) + " points of health.");
            if (ui.updatePlayerHp(-damageTaken))
                ui.setChoicesAndNextPositions("Continue", "", "", "", gameModel.position, "", "", "");
            else
                Toast.makeText(ui.getApplicationContext(), "You were unable to evade the monster's pursuit and suffered a blow, losing " + (damageTaken > 0 ? damageTaken : 0) + " points of health.", Toast.LENGTH_LONG).show();
        }
    }
}
