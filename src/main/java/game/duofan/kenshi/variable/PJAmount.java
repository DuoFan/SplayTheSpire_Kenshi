package game.duofan.kenshi.variable;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;

public class PJAmount extends DynamicVariable {
    @Override
    public String key() {
        return "duofan_kenshi:PJAmount";
        // What you put in your localization file between ! to show your value. Eg, !myKey!.
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return false;
        // Set to true if the value is modified from the base value.
    }

    @Override
    public void setIsModified(AbstractCard card, boolean v) {
        // Do something such that isModified will return the value v.
        // This method is only necessary if you want smith upgrade previews to function correctly.
    }

    @Override
    public int value(AbstractCard card) {
        return baseValue(card);
        // What the dynamic variable will be set to on your card. Usually uses some kind of int you store on your card.
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (!(card instanceof ITargetMonsterGetter)) {
            return 0;
        }
        AbstractMonster m = ((ITargetMonsterGetter) card).getTargetMonster();
        if (m == null) {
            return 0;
        }

        if(m.currentBlock <= 0){
            return 0;
        }

        return Math.min(card.damage * 3, m.currentBlock);
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
        // Should return true if the card was upgraded and the value was changed
    }
}