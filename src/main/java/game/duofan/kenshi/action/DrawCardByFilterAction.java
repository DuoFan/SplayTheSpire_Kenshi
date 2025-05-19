package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.FanShi;

public class DrawCardByFilterAction extends AbstractGameAction {

    ICardFilter filter;

    IDoCard doCard;

    public DrawCardByFilterAction(int amount, ICardFilter filter) {
        this.amount = amount;
        this.filter = filter;
    }

    public void setDoCard(IDoCard x) {
        doCard = x;
    }

    @Override
    public void update() {

        AbstractPlayer p = AbstractDungeon.player;

        CardGroup g = p.drawPile;

        if (p.hasPower(FanShi.POWER_ID)) {
            g = p.discardPile;
        }

        if (g.isEmpty()) {
            this.isDone = true;
            if (g == p.drawPile && !p.discardPile.isEmpty()) {
                DrawCardByFilterAction a = new DrawCardByFilterAction(amount, filter);
                a.setDoCard(doCard);
                addToTop(a);
                this.addToTop(new EmptyDeckShuffleAction());
            }
            return;
        }
        else if(Utils.needRefreshDiscardPileForDraw(filter,amount)){
            this.isDone = true;
            DrawCardByFilterAction a = new DrawCardByFilterAction(amount, filter);
            a.setDoCard(doCard);
            addToTop(a);
            this.addToTop(new EmptyDeckShuffleAction());
            return;
        }

        CardGroup filteredCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard card : g.group) {
            if (filter == null || filter.filter(card)) {
                filteredCards.addToBottom(card);
                if (filteredCards.size() >= amount) {
                    break;
                }
            }
        }

        if (!filteredCards.isEmpty()) {
            int drawCount = Math.min(this.amount, filteredCards.size());
            for (int i = 0; i < drawCount; i++) {
                AbstractCard card = filteredCards.group.get(i);
                g.removeCard(card);
                if(g == p.drawPile){
                    g.addToTop(card);
                }
                else{
                    g.addToBottom(card);
                }
            }

            AbstractGameAction follow = null;
            if (doCard != null) {
                follow = new DoCardAction(doCard, filteredCards, drawCount);
            }

            addToTop(new DrawCardAction(drawCount, follow));
        }

        this.isDone = true;
    }

    class DoCardAction extends AbstractGameAction {

        IDoCard doCard;
        CardGroup filterCardGroup;
        int drawCount;

        public DoCardAction(IDoCard doCard, CardGroup filterCardGroup, int drawCount) {
            this.doCard = doCard;
            this.filterCardGroup = filterCardGroup;
            this.drawCount = drawCount;
        }

        @Override
        public void update() {

            isDone = true;

            if (filterCardGroup != null && doCard != null) {
                for (int i = 0; i < drawCount; i++) {
                    AbstractCard c = filterCardGroup.group.get(i);
                    doCard.DoCard(c);
                }
            }
        }
    }
}