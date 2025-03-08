package game.duofan.kenshi.variable;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.AnYing;

public class QiLamagePlus extends DynamicVariable {
    @Override
    public String key() {
        return "duofan_kenshi:QiLamagePlus";
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
        int damage = card.damage;
        if (card instanceof IQiLamagePlus) {
            int qiAmount = Utils.getQiAmount();
            damage += qiAmount * ((IQiLamagePlus) card).getPlusPerQi();
        }
        return damage;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
        // Should return true if the card was upgraded and the value was changed
    }
}