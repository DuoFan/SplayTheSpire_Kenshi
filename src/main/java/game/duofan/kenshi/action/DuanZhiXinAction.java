package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.JianPei;

import java.util.ArrayList;
import java.util.Arrays;

public class DuanZhiXinAction extends AbstractGameAction {


    @Override
    public void update() {

        ArrayList<AbstractCard> cards = getEffectableCards();

        for (int i = 0; i < cards.size(); i++) {
            AbstractCard c = cards.get(i);
            addToBot(new DuanZaoAction(c, 1));
        }

        isDone = true;
    }

    ArrayList<AbstractCard> getEffectableCards() {
        ArrayList<AbstractCard> result = Utils.getCardsFromHand(0);

        for (int i = result.size() - 1; i >= 0; i--) {
            AbstractCard c = result.get(i);
            System.out.println(c.name + ":" + c.cardID.equals(JianPei.ID));
            if (!c.cardID.equals(JianPei.ID)) {
                result.remove(i);
            }
        }

        return result;
    }
}
