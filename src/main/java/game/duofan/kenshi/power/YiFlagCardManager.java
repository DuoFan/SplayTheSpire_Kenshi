package game.duofan.kenshi.power;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class YiFlagCardManager extends TagCardManager {
    private static YiFlagCardManager instance;

    public static YiFlagCardManager getInstance() {
        if (instance == null) {
            instance = new YiFlagCardManager();
        }
        return instance;
    }

    @Override
    protected AbstractCard.CardTags getTag() {
        return YiFlag;
    }

    @SpireEnum
    public static AbstractCard.CardTags YiFlag;
}