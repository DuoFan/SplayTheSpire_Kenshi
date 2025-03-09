package game.duofan.kenshi.action;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Claw;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.kenshi.card.FZL_QiuYeLianJian;

import java.util.Iterator;

public class QiuYeLianJianAction extends AbstractGameAction {
    private AbstractCard card;

    public QiuYeLianJianAction(AbstractCard card, int amount) {
        this.card = card;
        this.amount = amount;
    }

    public void update() {
        this.card.baseDamage += this.amount;
        this.card.applyPowers();
        Iterator var1 = AbstractDungeon.player.discardPile.group.iterator();

        AbstractCard c;
        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if (c instanceof FZL_QiuYeLianJian) {
                c.baseDamage += this.amount;
                c.applyPowers();
            }
        }

        var1 = AbstractDungeon.player.drawPile.group.iterator();

        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if (c instanceof FZL_QiuYeLianJian) {
                c.baseDamage += this.amount;
                c.applyPowers();
            }
        }

        var1 = AbstractDungeon.player.hand.group.iterator();

        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if (c instanceof FZL_QiuYeLianJian) {
                c.baseDamage += this.amount;
                c.applyPowers();
            }
        }

        this.isDone = true;
    }
}
