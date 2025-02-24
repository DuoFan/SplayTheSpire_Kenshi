package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.kenshi.power.Ji_FanShi;

public class DrawCardByFilterAction extends AbstractGameAction {

    ICardFilter filter;

    public DrawCardByFilterAction(int amount, ICardFilter filter) {
        this.amount = amount;
        this.filter = filter;
    }

    @Override
    public void update() {

        AbstractPlayer p = AbstractDungeon.player;

        CardGroup g = p.drawPile;

        if(p.hasPower(Ji_FanShi.POWER_ID)){
            g = p.discardPile;
        }

        if (g.isEmpty()) {
            this.isDone = true;
            return;
        }

        CardGroup filteredCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard card : g.group) {
            if (filter == null || filter.filter(card)) {
                filteredCards.addToBottom(card);
                if(filteredCards.size() >= amount){
                    break;
                }
            }
        }

        if (!filteredCards.isEmpty()) {
            int drawCount = Math.min(this.amount, filteredCards.size());
            for (int i = 0; i < drawCount; i++) {
                AbstractCard card = filteredCards.getTopCard();
                filteredCards.removeTopCard();

                if (p.hand.size() < 10) {
                    g.moveToHand(card, g);
                } else {
                    if(g.equals(p.drawPile)){
                        p.drawPile.moveToDiscardPile(card);
                    }
                    p.createHandIsFullDialog();
                }
            }
        }

        this.isDone = true;
    }
}