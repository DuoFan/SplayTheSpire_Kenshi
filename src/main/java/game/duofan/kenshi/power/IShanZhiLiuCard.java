package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface IShanZhiLiuCard extends IUpdateDescription, ILiuCard {
    void shanZhiLiuEffect();

    boolean effectable();
    boolean isExchangeAble();
    void setToExchange();
}

