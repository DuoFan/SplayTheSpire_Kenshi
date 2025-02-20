package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;
import game.duofan.common.Utils;

public abstract class TagCardManager {

    protected abstract AbstractCard.CardTags getTag();

    public void addCard(AbstractCard c) {
        AbstractCard.CardTags tag = getTag();
        if (!c.tags.contains(tag)) {
            c.tags.add(tag);
            System.out.println(c.name + "被设置" + tag.name());
        }
    }

    public void removeCard(AbstractCard c) {
        AbstractCard.CardTags tag = getTag();
        if (c.tags.remove(tag)) {
            System.out.println(c.name + "被移除" + tag.name());
        }
    }

    public boolean isTagCard(AbstractCard c) {
        return c.tags.contains(getTag());
    }

}