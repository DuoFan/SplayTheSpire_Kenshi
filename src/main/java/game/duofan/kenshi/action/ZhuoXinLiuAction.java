package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.IDoCard;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.RongRong;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ZhuoXinLiuAction extends AbstractGameAction {

    AbstractMonster targetMonster;

    public ZhuoXinLiuAction(int _amount, AbstractMonster m) {
        amount = _amount;
        targetMonster = m;
    }

    public void update() {
        isDone = true;

        if (amount > 0) {
            List<AbstractMonster> monsters = Utils.sortMonsterByXPos(Utils.getAllAliveMonsters());
            int index = monsters.indexOf(targetMonster);
            if (index >= 0) {
                Stack<Integer> rongrongGive = new Stack<Integer>();
                Stack<AbstractMonster> monsterStack = new Stack<>();
                int _amount = amount;
                rongrongGive.push(_amount);
                monsterStack.push(targetMonster);
                int count = 1;
                int size = monsters.size();
                while (_amount > 1) {
                    int left = index - count;
                    int right = index + count;
                    if (left >= 0) {
                        rongrongGive.push(_amount / 2);
                        monsterStack.push(monsters.get(left));
                    }
                    if (right < size) {
                        rongrongGive.push(_amount / 2);
                        monsterStack.push(monsters.get(right));
                    }
                    _amount /= 2;
                    count++;
                }

                AbstractPlayer p = AbstractDungeon.player;
                while (!monsterStack.empty()) {
                    AbstractMonster m = monsterStack.pop();
                    Utils.givePowerTop(p, m, new RongRong(m, rongrongGive.pop()));
                }

            } else {
                Utils.givePowerTop(AbstractDungeon.player, targetMonster, new RongRong(targetMonster, amount));
            }
        }
    }
}