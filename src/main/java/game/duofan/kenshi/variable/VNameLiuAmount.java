package game.duofan.kenshi.variable;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.ICardFilter;
import game.duofan.kenshi.power.Liu_StateMachine;

public class VNameLiuAmount extends DynamicVariable implements ICardFilter {
    @Override
    public String key() {
        return "duofan_kenshi:VNameLiuAmount";
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

    AbstractCard checkCard;

    @Override
    public int baseValue(AbstractCard card) {
        checkCard = card;
        return Utils.stasticsCardPlayedInTurn(this);
        // Should generally just be the above.
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
        // Should return true if the card was upgraded and the value was changed
    }

    @Override
    public boolean filter(AbstractCard c) {
        return !c.name.equals(checkCard.name) && Utils.getLiuFromCard(c) != Liu_StateMachine.StateEnum.None;
    }
}
