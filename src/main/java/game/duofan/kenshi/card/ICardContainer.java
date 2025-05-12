package game.duofan.kenshi.card;

import com.megacrit.cardcrawl.cards.CardGroup;

public interface ICardContainer {
    CardGroup getContainer();
    float[] getCardOffsetsX();
    float[] getCardOffsetsY();
    float getCardScale();
}
