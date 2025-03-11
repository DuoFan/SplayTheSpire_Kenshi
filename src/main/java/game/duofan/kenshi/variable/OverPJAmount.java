package game.duofan.kenshi.variable;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class OverPJAmount extends DynamicVariable {
    @Override
    public String key() {
        return "duofan_kenshi:OverPJAmount";
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
            return card.damage;
        }
        AbstractMonster m = ((ITargetMonsterGetter) card).getTargetMonster();
        if (m == null) {
            return card.damage;
        }

        if(m.currentBlock <= 0){
            return card.damage;
        }

        int amount = 0;
        int expectPJAmount = card.damage * 3;
        if (expectPJAmount > m.currentBlock) {
            int sub = expectPJAmount - m.currentBlock;
            amount += sub / 3;
            if (sub % 3 != 0) {
                amount++;
            }
        }
        return amount;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
        // Should return true if the card was upgraded and the value was changed
    }
}