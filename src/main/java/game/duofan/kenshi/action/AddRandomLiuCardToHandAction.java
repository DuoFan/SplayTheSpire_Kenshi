package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.ArrayList;

public class AddRandomLiuCardToHandAction extends AbstractGameAction {

    Liu_StateMachine.StateEnum liu;

    public AddRandomLiuCardToHandAction(Liu_StateMachine.StateEnum _liu) {
        liu = _liu;
    }

    @Override
    public void update() {
        isDone = true;
        if (liu == Liu_StateMachine.StateEnum.None) {
            return;
        }

        int rng = AbstractDungeon.cardRandomRng.random(0, 10);
        AbstractCard.CardRarity r = AbstractCard.CardRarity.COMMON;
        if (rng >= 8) {
            r = AbstractCard.CardRarity.RARE;
        } else if (rng >= 4) {
            r = AbstractCard.CardRarity.UNCOMMON;
        }
        AbstractCard c = null;
        ArrayList<AbstractCard> cards = Utils.getCardsFromLiu(liu.getValue());
        while (c == null) {
            c = Utils.getRandomCardsFromList(cards, true);
            if (c.rarity != r) {
                c = null;
            }
        }
        AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c, 1));
    }
}
