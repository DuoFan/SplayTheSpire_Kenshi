package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.RongRong;
import game.duofan.kenshi.variable.ITargetMonsterGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class YanJieAction extends AbstractGameAction {

    int energyOnUse;
    boolean isUpgraded;
    public int extraEffect;
    Boolean freeToPlayOnce;

    public YanJieAction(int _amount, int _energyOnUse, boolean _isUpgraded, Boolean _freeToPlayOnce, AbstractMonster _targetMonster) {
        amount = _amount;
        isUpgraded = _isUpgraded;
        energyOnUse = _energyOnUse;
        target = _targetMonster;
        freeToPlayOnce = _freeToPlayOnce;
    }

    public void update() {
        isDone = true;

        AbstractPlayer p = AbstractDungeon.player;

        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }

        if (p.hasRelic(ChemicalX.ID)) {
            effect += 2;
            p.getRelic(ChemicalX.ID).flash();
        }

        if (isUpgraded) {
            effect++;
        }

        effect += extraEffect;

        if (effect > 0) {
            while (effect > 0) {
                Utils.giveBaoYanDamage(p, target, amount, DamageInfo.DamageType.NORMAL);
                effect--;
            }
            if (!freeToPlayOnce && energyOnUse > 0) {
                p.energy.use(EnergyPanel.totalCount);
            }
        }
    }
}