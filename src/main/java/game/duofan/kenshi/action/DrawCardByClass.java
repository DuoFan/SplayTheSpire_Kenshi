package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import java.util.Iterator;

public class DrawCardByClass extends AbstractGameAction {
    private final Class<?> targetClass;  // 改用 Class<?> 类型

    public DrawCardByClass(int amount, Class<?> targetClass) {
        this.amount = amount;
        this.targetClass = targetClass;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;

        if (p.drawPile.isEmpty()) {
            this.isDone = true;
            return;
        }

        CardGroup filteredCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard card : p.drawPile.group) {
            // 使用 isInstance() 替代 instanceof
            if (targetClass.isInstance(card)) {
                filteredCards.addToTop(card);
            }
        }

        if (!filteredCards.isEmpty()) {
            int drawCount = Math.min(this.amount, filteredCards.size());
            for (int i = 0; i < drawCount; i++) {
                AbstractCard card = filteredCards.getTopCard();
                filteredCards.removeTopCard();

                if (p.hand.size() < 10) {
                    p.drawPile.moveToHand(card, p.drawPile);
                } else {
                    p.drawPile.moveToDiscardPile(card);
                    p.createHandIsFullDialog();
                }
            }
        }

        this.isDone = true;
    }
}