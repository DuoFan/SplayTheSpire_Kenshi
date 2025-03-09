package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.RongRong;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class YanLangAction extends AbstractGameAction {

    int energyOnUse;

    public YanLangAction(int _amount, int _energyOnUse) {
        amount = _amount;
        energyOnUse = _energyOnUse;
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

        ArrayList<AbstractMonster> targets = Utils.getAllAliveMonsters();
        if (effect > 0) {
            while (effect > 0) {
                for (int i = 0; i < targets.size(); i++) {
                    Utils.givePower(p, targets.get(i), new RongRong(targets.get(i), amount));
                }
                effect--;
            }
            if (energyOnUse > 0) {
                p.energy.use(EnergyPanel.totalCount);
            }
        }
    }
}