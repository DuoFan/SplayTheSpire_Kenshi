package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.DisablePower;

import java.util.ArrayList;

public class DisableRandomPowerAction extends AbstractGameAction {

    boolean debuff;

    public DisableRandomPowerAction(AbstractCreature _target, int _amount, boolean isDebuff) {
        this.target = _target;
        this.amount = _amount;
        debuff = isDebuff;
    }

    public void update() {
        isDone = true;

        if (amount <= 0) {
            return;
        }

        if (target == null || target.powers == null) {
            return;
        }

        ArrayList<AbstractPower> debuffList = new ArrayList<>();
        for (int i = 0; i < target.powers.size(); i++) {
            AbstractPower p = target.powers.get(i);
            if (p == null || p instanceof DisablePower) {
                continue;
            }

            if (debuff && p.type != AbstractPower.PowerType.DEBUFF) {
                continue;
            }
            else if(!debuff && p.type == AbstractPower.PowerType.DEBUFF){
                continue;
            }

            debuffList.add(p);
        }

        if (debuffList.size() <= 0) {
            return;
        }

        int index = AbstractDungeon.cardRandomRng.random(debuffList.size() - 1);
        AbstractPower originPower = debuffList.get(index);
        DisablePower disablePower = new DisablePower(originPower);
        Utils.gainPowerTop(disablePower.owner, disablePower);

        amount--;
        if (amount > 0) {
            addToBot(new DisableRandomPowerAction(target, amount, debuff));
        }
    }
}