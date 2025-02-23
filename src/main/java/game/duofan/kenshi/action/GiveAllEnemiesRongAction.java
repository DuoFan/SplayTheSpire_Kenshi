package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.RongRong;

import java.util.ArrayList;

public class GiveAllEnemiesRongAction extends AbstractGameAction {

    public GiveAllEnemiesRongAction(int _amount) {
        amount = _amount;
    }

    public void update() {
        if (amount > 0) {
            ArrayList<AbstractMonster> targets = Utils.getAllAliveMonsters();
            AbstractPlayer p = AbstractDungeon.player;
            for (int i = 0; i < targets.size(); i++) {
                Utils.givePower(p, targets.get(i), new RongRong(targets.get(i), amount));
            }
        }

        isDone = true;
    }
}