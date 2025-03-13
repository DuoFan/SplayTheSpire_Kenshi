package game.duofan.kenshi.action;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;

import java.util.Iterator;

public class TuoTaiHuanGuAction extends AbstractGameAction {
    private AbstractPlayer p;

    public TuoTaiHuanGuAction(int amount) {
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
        this.amount = amount;
    }

    public void update() {
        Iterator var1;
        AbstractCard c;

        var1 = this.p.hand.group.iterator();

        int eAmount = 0;
        while (var1.hasNext()) {
            c = (AbstractCard) var1.next();
            if (c.canUpgrade()) {
                c.upgrade();
                c.superFlash();
                c.applyPowers();
                eAmount++;
            }
        }

        if (eAmount > 0) {
            Utils.playerGainEnergy(eAmount * amount);
        }

        this.isDone = true;
    }
}
