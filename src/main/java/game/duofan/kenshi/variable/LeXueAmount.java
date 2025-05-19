package game.duofan.kenshi.variable;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.LeXue;
import game.duofan.kenshi.power.FanShi;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.HashSet;

public class LeXueAmount extends DynamicVariable {
    @Override
    public String key() {
        return "duofan_kenshi:LeXueAmount";
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
        if (!(card instanceof LeXue)) {
            return 0;
        }

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return 0;
        }

        CardGroup hand = p.hand;

        HashSet<Liu_StateMachine.StateEnum> liuSet = new HashSet<>();
        liuSet.add(Liu_StateMachine.StateEnum.None);

        if (hand != null) {
            for (int i = 0; i < hand.size(); i++) {
                AbstractCard c = p.hand.getNCardFromTop(i);
                liuSet.add(Utils.getLiuFromCard(c));
            }
        }

        int count1 = liuSet.size();

        Utils.calculateRefreshDiscardPileForDraw((c) ->{
            return liuSet.add(Utils.getLiuFromCard(c));
        });

        return liuSet.size() - count1;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
        // Should return true if the card was upgraded and the value was changed
    }
}