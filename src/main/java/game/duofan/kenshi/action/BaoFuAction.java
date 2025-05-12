package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;

public class BaoFuAction extends AbstractGameAction {

    CardGroup g;
    AbstractPlayer p;

    public BaoFuAction(CardGroup _g) {
        g = _g;
    }

    public void update() {
        isDone = true;
        if(g == null){
            return;
        }
        p = AbstractDungeon.player;
        for (int i = 0; i < g.size(); i++) {
            AbstractCard card = g.getNCardFromTop(i);
            Utils.addToTopAbstract(()->{
                card.targetDrawScale = 1;
                if (this.p.hand.size() == 10) {
                    g.moveToDiscardPile(card);
                    this.p.createHandIsFullDialog();
                } else {
                    card.unhover();
                    card.lighten(true);
                    card.setAngle(0.0F);
                    card.drawScale = 0.12F;
                    card.targetDrawScale = 0.75F;
                    card.current_x = CardGroup.DRAW_PILE_X;
                    card.current_y = CardGroup.DRAW_PILE_Y;
                    g.removeCard(card);
                    AbstractDungeon.player.hand.addToTop(card);
                    AbstractDungeon.player.hand.refreshHandLayout();
                    AbstractDungeon.player.hand.applyPowers();
                }
            });
        }
    }
}
