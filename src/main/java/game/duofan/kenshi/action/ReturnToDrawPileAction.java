package game.duofan.kenshi.action;

import com.evacipated.cardcrawl.mod.stslib.actions.common.MoveCardsAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;

public class ReturnToDrawPileAction extends AbstractGameAction {

    AbstractCard card;

    public ReturnToDrawPileAction(AbstractCard _card) {
        card = _card;
    }

    @Override
    public void update() {
        this.isDone = true;

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return;
        }

        CardGroup drawPile = p.drawPile;
        if (drawPile == null) {
            return;
        }

        if (drawPile.contains(card)) {
            return;
        }

        CardGroup g = null;
        if (p.hand.contains(card)) {
            g = p.hand;
        } else if (p.discardPile.contains(card)) {
            g = p.discardPile;
        }

        if (g == null) {
            return;
        }

        g.moveToDeck(card, true);
    }

}
