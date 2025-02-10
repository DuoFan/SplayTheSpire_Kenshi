package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface IShanZhiLiuCard extends IUpdateDescription {
    void shanZhiLiuEffect();
    boolean effectable();
    public void setLinkedCardHoverPreview(AbstractCard c);
}

