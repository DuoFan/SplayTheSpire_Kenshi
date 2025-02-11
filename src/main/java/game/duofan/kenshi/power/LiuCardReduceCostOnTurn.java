package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.FreeAttackPower;

import java.util.ArrayList;

public abstract class LiuCardReduceCostOnTurn extends AbstractPower {

    protected Class liu;
    protected String[] descriptions;

    ArrayList<AbstractCard> cards;

    public LiuCardReduceCostOnTurn(AbstractCreature owner, int amount) {
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
    }

    protected void effect() {
        cards = new ArrayList<>();
        for (int i = 0; i < AbstractDungeon.player.hand.size(); i++) {
            AbstractCard c = AbstractDungeon.player.hand.group.get(i);
            if (liu.isInstance(c) && c.costForTurn > 0) {
                cards.add(c);
                c.setCostForTurn(c.costForTurn - 1);
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = String.format(descriptions[0], amount);
    }

    void restore() {
        for (int i = 0; i < cards.size(); i++) {
            AbstractCard c = AbstractDungeon.player.hand.group.get(i);
            c.setCostForTurn(c.costForTurn + 1);
        }
        cards = null;
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (liu.isInstance(card) && !card.purgeOnUse && this.amount > 0) {
            this.flash();
            --this.amount;
            if (this.amount == 0) {
                restore();
                this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, "FreeAttackPower"));
            }
        }
    }
}
