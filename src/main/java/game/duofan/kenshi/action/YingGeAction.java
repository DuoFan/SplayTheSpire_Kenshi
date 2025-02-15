package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.YuZL_YingGe;

public class YingGeAction extends AbstractGameAction {

    int blockAmount;
    YuZL_YingGe self;

    public YingGeAction(YuZL_YingGe _self, int _amount, int _blockAmount) {
        self = _self;
        amount = _amount;
        blockAmount = _blockAmount;
    }

    public void update() {
        Utils.addToTopAbstract(() -> {
            CardGroup g = AbstractDungeon.player.hand;
            if (g.size() > 0) {
                AbstractCard c = g.getTopCard();
                if (c != null && c.type == AbstractCard.CardType.SKILL) {
                    Utils.playerGainBlock(blockAmount);
                    self.setSuccess(true);
                }
                else{
                    self.setSuccess(false);
                }
            }
        });
        addToTop(new DrawCardAction(1));

        this.tickDuration();
    }
}