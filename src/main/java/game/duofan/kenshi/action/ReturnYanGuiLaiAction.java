package game.duofan.kenshi.action;

import com.evacipated.cardcrawl.mod.stslib.actions.common.MoveCardsAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.YuZL_YanGuiLai;

public class ReturnYanGuiLaiAction extends AbstractGameAction {

    @Override
    public void update() {
        this.isDone = true;

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return;
        }

        CardGroup discardPile = p.discardPile;
        if (discardPile == null) {
            return;
        }

        String yanGuiLaiID = YuZL_YanGuiLai.ID;

        for (int i = 0; i < discardPile.size(); i++) {
            AbstractCard c = discardPile.group.get(i);
            if (c.cardID.equals(yanGuiLaiID)) {
                addToBot(new DiscardToHandAction(c));
            }
        }
    }

}
