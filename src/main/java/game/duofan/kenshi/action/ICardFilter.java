package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface ICardFilter {
    boolean filter(AbstractCard c);
}
