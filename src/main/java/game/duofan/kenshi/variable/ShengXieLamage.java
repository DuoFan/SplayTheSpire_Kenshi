package game.duofan.kenshi.variable;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import game.duofan.kenshi.card.XZL_ShengXie;

public class ShengXieLamage extends DynamicVariable {
    @Override
    public String key() {
        return "duofan_kenshi:ShengXieLamage";
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
        if (!(card instanceof XZL_ShengXie)) {
            return 0;
        }

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return 0;
        }

        XZL_ShengXie c = (XZL_ShengXie) card;

        AbstractMonster m = c.getTargetMonster();
        if (m == null) {
            return 0;
        }

        int strengthDiff = 0;
        /*if (!c.getIsPlayed()) {
            strengthDiff += 2;
            AbstractPower power = m.getPower(ArtifactPower.POWER_ID);
            if(power != null){
                strengthDiff = 0;
            }
        }*/

        AbstractPower strength = p.getPower(StrengthPower.POWER_ID);
        if(strength != null){
            strengthDiff += strength.amount;
        }

        strength = m.getPower(StrengthPower.POWER_ID);
        if(strength != null){
            strengthDiff -= strength.amount;
        }

        int damage = 0;
        if(strengthDiff > 0){
            damage = strengthDiff * c.magicNumber;
        }

        return damage;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
        // Should return true if the card was upgraded and the value was changed
    }
}