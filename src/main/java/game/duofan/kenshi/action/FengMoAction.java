package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.DisablePower;

import java.util.ArrayList;
import java.util.Iterator;

public class FengMoAction extends AbstractGameAction {

    AbstractCreature target;
    int turns;
    int damage;

    public FengMoAction(AbstractCreature _target, int _damage, int _turns) {
        target = _target;
        turns = _turns;
        damage = _damage;
    }

    public void update() {
        isDone = true;

        ArrayList<AbstractPower> powers = target.powers;

        Utils.addToTopAbstract(() -> {
            Iterator<AbstractPower> iterator = powers.iterator();
            while (iterator.hasNext()) {
                AbstractPower p = iterator.next();
                if (p instanceof DisablePower) {
                    Utils.giveDamageTop(AbstractDungeon.player, target, damage,
                            DamageInfo.DamageType.NORMAL, AttackEffect.BLUNT_LIGHT);
                }
            }
        });

        Iterator<AbstractPower> iterator = powers.iterator();
        while (iterator.hasNext()) {
            AbstractPower originPower = iterator.next();
            if (originPower.type != AbstractPower.PowerType.DEBUFF && !(originPower instanceof DisablePower)) {
                addToTop(new DisablePowerAction(originPower, turns));
            }
        }
    }
}
