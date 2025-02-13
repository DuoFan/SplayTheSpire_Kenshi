package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.IDoCard;
import game.duofan.kenshi.power.IExtraDuanZaoEffect;
import game.duofan.kenshi.power.IShanZhiLiuCard;

import java.util.ArrayList;

public class DuanZaoAction extends AbstractGameAction {

    AbstractCard card;

    public DuanZaoAction(AbstractCard c, int _amount) {
        card = c;
        amount = _amount;
    }

    public void update() {

        if (card != null) {
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
                }
            }

            if(card instanceof IExtraDuanZaoEffect){
                ((IExtraDuanZaoEffect)card).extraDuanZaoEffect();
            }

            if (card instanceof IShanZhiLiuCard) {
                Utils.updateSZL_Description((IShanZhiLiuCard) card);
            } else {
                card.initializeDescription();
            }

            EventManager.getInstance().notifyEvent(EventKey.ON_CARD_BE_DUANZAO, this, card);
        }

        isDone = true;
    }
}