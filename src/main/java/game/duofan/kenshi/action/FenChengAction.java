package game.duofan.kenshi.action;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.RongRong;

import java.util.ArrayList;
import java.util.Iterator;

public class FenChengAction extends AbstractGameAction {

    public FenChengAction() {
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        this.tickDuration();
        if (this.isDone) {
            Utils.giveAllMonsterBaoYanDamage(1);

            ArrayList<AbstractMonster> monsters = Utils.getAllAliveMonsters();
            boolean stop = true;
            for (int i = 0; i < monsters.size(); i++) {
                AbstractMonster m = monsters.get(i);
                AbstractPower rongRong = m.getPower(RongRong.POWER_ID);
                if (rongRong != null && rongRong.amount > 0) {
                    if (rongRong.amount <= 2) {
                        Utils.removePower(m, RongRong.POWER_ID);
                    } else {
                        Utils.gainPower(m, new RongRong(m, -2));
                        stop = false;
                    }
                }
            }

            if (!stop) {
                this.addToBot(new FenChengAction());
            }

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }

    }
}
