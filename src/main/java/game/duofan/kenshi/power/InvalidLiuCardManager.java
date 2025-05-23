package game.duofan.kenshi.power;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;

import java.util.HashSet;

public class InvalidLiuCardManager extends TagCardManager {
    private static InvalidLiuCardManager instance;

    public static InvalidLiuCardManager getInstance() {
        if (instance == null) {
            instance = new InvalidLiuCardManager();
        }
        return instance;
    }

    @Override
    protected AbstractCard.CardTags getTag() {
        return InvalidLiu;
    }

    @SpireEnum
    public static AbstractCard.CardTags InvalidLiu;
}