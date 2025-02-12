package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.IDoCard;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.AbstractList;
import java.util.ArrayList;

public class QianBianWanHuaAction extends AbstractGameAction {

    public QianBianWanHuaAction(int _amount) {
        amount = _amount;
    }

    public void update() {
        if (amount > 0) {
            ArrayList<AbstractCard> cards = Utils.getCardsFromLiu(Liu_StateMachine.StateEnum.All.getValue());
            Liu_StateMachine.StateEnum curLiu = Liu_StateMachine.getInstance().getLiu();
            while (amount > 0) {
                AbstractCard c = Utils.getRandomCardsFromList(cards, false);
                Liu_StateMachine.StateEnum cardLiu = Utils.getLiuFromCard(c);
                System.out.println(curLiu + "->" + cardLiu + ":" + c.name);
                if (curLiu == Liu_StateMachine.StateEnum.None || curLiu != cardLiu) {
                    curLiu = cardLiu;
                    amount--;
                }
                c.exhaustOnUseOnce = true;
                addToBot(new NewQueueCardAction(c, true, false, true));
            }
        }

        isDone = true;
    }
}