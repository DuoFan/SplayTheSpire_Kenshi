package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.GuoYanYunYan;
import game.duofan.kenshi.power.Liu_StateMachine;

public class GuoYanYunYanAction extends AbstractGameAction {

    public GuoYanYunYanAction(int _amount) {
        amount = _amount;
    }

    public void update() {
        isDone = true;

        CardGroup hand = AbstractDungeon.player.hand;
        for (int i = 0; i < hand.size(); i++) {
            AbstractCard c = hand.getNCardFromTop(i);

            Liu_StateMachine.StateEnum liu = Utils.getLiuFromCard(c);
            if (liu != Liu_StateMachine.StateEnum.None) {
                for (int j = 0; j < amount; j++) {
                    Utils.addToTopAbstract(() -> {
                        Utils.invokeLiuCardEffect(c);
                    });
                }
                addToTop(new ExhaustSpecificCardAction(c, hand));
            }
        }
    }
}
