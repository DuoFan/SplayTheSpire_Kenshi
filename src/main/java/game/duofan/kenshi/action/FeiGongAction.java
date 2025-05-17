package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.unique.UnloadAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.FeiGong;

import java.util.Iterator;

public class FeiGongAction extends AbstractGameAction {

    FeiGong self;

    public FeiGongAction(FeiGong _self) {
        self = _self;
    }

    public void update() {
        isDone = true;

        int amount = 0;

        Iterator var1 = AbstractDungeon.player.hand.group.iterator();
        while(var1.hasNext()) {
            AbstractCard c = (AbstractCard)var1.next();
            if (c.type == AbstractCard.CardType.ATTACK) {
                amount++;
            }
        }

        if(self.upgraded){
            amount++;
        }

        if(amount > 0){
            addToTop(new DrawCardAction(amount));
        }

        var1 = AbstractDungeon.player.hand.group.iterator();
        while(var1.hasNext()) {
            AbstractCard c = (AbstractCard)var1.next();
            if (c.type == AbstractCard.CardType.ATTACK) {
                this.addToTop(new DiscardSpecificCardAction(c));
            }
        }
    }
}
