package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.RongRong;
import jdk.nashorn.internal.objects.annotations.Where;

import java.util.List;
import java.util.Stack;

public class HuoZhiYuAction extends AbstractGameAction {

    AbstractMonster targetMonster;

    public HuoZhiYuAction(int _amount, AbstractMonster m) {
        amount = _amount;
        targetMonster = m;
    }

    public void update() {
        isDone = true;

        if (amount > 0) {
            List<AbstractMonster> monsters = Utils.sortMonsterByXPos(AbstractDungeon.getMonsters().monsters);
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

                    while (left >= 0 && monsters.get(left).isDeadOrEscaped()) {
                        left--;
                    }

                    if (left >= 0) {
                        rongrongGive.push(_amount / 2);
                        monsterStack.push(monsters.get(left));
                    }

                    while (right < size && monsters.get(right).isDeadOrEscaped()) {
                        right++;
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