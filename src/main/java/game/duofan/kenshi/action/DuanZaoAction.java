package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.kenshi.power.IUpdateDescription;

public class DuanZaoAction extends AbstractGameAction {

    AbstractCard card;

    public DuanZaoAction(AbstractCard c, int _amount) {
        card = c;
        amount = _amount;
    }

    public void update() {

        if (card != null) {
            boolean isOk = true;
            for (int i = 0; i < amount; i++) {
                switch (card.type) {
                    case ATTACK:
                        card.baseDamage += 2;
                        break;
                    case SKILL:
                        if (!card.selfRetain) {
                            card.selfRetain = true;
                            card.rawDescription += " NL 保留 ";
                        }
                        break;
                    case POWER:
                        if (card.cost > 0) {
                            card.modifyCostForCombat(-1);
                        }
                        break;
                    default:
                        isOk = false;
                        break;
                }
            }

            /*if (card instanceof IExtraDuanZaoEffect) {
                ((IExtraDuanZaoEffect) card).extraDuanZaoEffect();
                isOk = true;
            }*/

            if (isOk) {
                if (card instanceof IUpdateDescription) {
                    ((IUpdateDescription) card).updateDescription();
                } else {
                    card.initializeDescription();
                }
                card.superFlash();
            }

            EventManager.getInstance().notifyEvent(EventKey.ON_CARD_BE_DUANZAO, this, card);
        }

        isDone = true;
    }
}