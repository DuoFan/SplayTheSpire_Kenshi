package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.unique.UnloadAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.FeiGong;
import game.duofan.kenshi.card.FengMo;
import game.duofan.kenshi.power.DisablePower;

import java.util.ArrayList;
import java.util.Iterator;

public class DisablePowerAction extends AbstractGameAction {

    AbstractPower power;
    int turns;

    public DisablePowerAction(AbstractPower _power, int _turns) {
        power = _power;
        turns = _turns;
    }

    public void update() {
        isDone = true;

        if(power == null){
            return;
        }

        if(power instanceof DisablePower){
            return;
        }

        AbstractCreature owner = power.owner;
        int index = owner.powers.indexOf(power);
        if(index < 0){
            return;
        }

        DisablePower disablePower = new DisablePower(power);
        Utils.gainPowerTop(disablePower.owner, disablePower);
    }
}
