package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import game.duofan.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class DuanZaoHandAction extends AbstractGameAction {

    AbstractCard.CardType[] includeCardTypes;
    int amount;

    public DuanZaoHandAction(AbstractCard.CardType[] types, int _amount) {
        includeCardTypes = types;
        amount = _amount;
    }

    @Override
    public void update() {
        if (amount > 0) {
            ArrayList<AbstractCard> cards = getEffectableCards();

            while (amount > 0 && !cards.isEmpty()){
                AbstractCard c = Utils.getRandomCardsFromList(cards,false);
                if(c.type == AbstractCard.CardType.SKILL){
                    cards.remove(c);
                }
                else if(c.type == AbstractCard.CardType.POWER && c.cost <= 1){
                    cards.remove(c);
                }
                addToBot(new DuanZaoAction(c,1));
                amount--;
            }
        }

        isDone = true;
    }

    ArrayList<AbstractCard> getEffectableCards() {
        ArrayList<AbstractCard> result = Utils.getCardsFromHand(0);

        for (int i = result.size() - 1; i >= 0; i--) {
            AbstractCard c = result.get(i);
            if (includeCardTypes != null && Arrays.binarySearch(includeCardTypes,c.type) < 0) {
                result.remove(i);
                continue;
            } else if (c.type != AbstractCard.CardType.ATTACK
                    && c.type != AbstractCard.CardType.SKILL
                    && c.type != AbstractCard.CardType.POWER) {
                result.remove(i);
                continue;
            }

            if(c.type == AbstractCard.CardType.SKILL && c.selfRetain){
                result.remove(i);
                continue;
            }

            if(c.type == AbstractCard.CardType.POWER && c.cost <= 0)
            {
                result.remove(i);
                continue;
            }
        }

        return result;
    }
}
