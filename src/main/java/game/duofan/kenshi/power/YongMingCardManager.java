package game.duofan.kenshi.power;

import basemod.BaseMod;
import basemod.helpers.CardTags;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;
import game.duofan.common.Utils;

import java.util.HashSet;

public class YongMingCardManager extends TagCardManager {
    private static YongMingCardManager instance;

    public static YongMingCardManager getInstance() {
        if (instance == null) {
            instance = new YongMingCardManager();
        }
        return instance;
    }

    @Override
    protected AbstractCard.CardTags getTag() {
        return YONG_MING;
    }

    public YongMingCardManager() {
        super();
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_TURN_START_POST_DRAW, new TurnStartPostDrawListener());
    }


    @SpireEnum
    public static AbstractCard.CardTags YONG_MING;

    class TurnStartPostDrawListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            Utils.addToBotAbstract(() -> {
                CardGroup g = AbstractDungeon.player.drawPile;
                for (int i = g.size() - 1; i >= 0; i--) {
                    AbstractCard c = g.group.get(i);
                    if (isTagCard(c)) {
                        g.moveToHand(c);
                        Utils.addToBotAbstract(() -> {
                            LinkCardManager.getInstance().tryDrawLinkedCard(c);
                        });
                    }
                }
                CardGroup g2 = AbstractDungeon.player.discardPile;
                for (int i = g2.size() - 1; i >= 0; i--) {
                    AbstractCard c = g2.group.get(i);
                    if (isTagCard(c)) {
                        g2.moveToHand(c);
                        Utils.addToBotAbstract(() -> {
                            LinkCardManager.getInstance().tryDrawLinkedCard(c);
                        });
                    }
                }
            });
        }
    }
}