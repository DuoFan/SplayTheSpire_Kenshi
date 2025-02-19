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

public class YongMingCardManager {
    private static YongMingCardManager instance;

    public static YongMingCardManager getInstance() {
        if (instance == null) {
            instance = new YongMingCardManager();
        }
        return instance;
    }

    public YongMingCardManager() {
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_BATTLE_START, new BattleStartListener());
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_TURN_START_POST_DRAW, new TurnStartPostDrawListener());
    }

    public void clear() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return;
        }
        clearFromGroup(p.masterDeck);
    }

    void clearFromGroup(CardGroup group) {
        if (group == null) {
            return;
        }

        for (int i = 0; i < group.size(); i++) {
            removeCard(group.group.get(i));
        }
    }

    public void addCard(AbstractCard c) {
        if (!c.tags.contains(YONG_MING)) {
            c.tags.add(YONG_MING);
            System.out.println(c.name + "被设置为永明卡");
        }
    }

    public void removeCard(AbstractCard c) {
        if (c.tags.remove(YONG_MING)) {
            System.out.println(c.name + "被移除永明卡");
        }
    }

    public boolean isYongMingCard(AbstractCard c) {
        return c.tags.contains(YONG_MING);
    }


    @SpireEnum
    public static AbstractCard.CardTags YONG_MING;

    class BattleStartListener implements IEventListener {
        @Override
        public void OnEvent(Object sender, Object e) {
            clear();
            CardGroup g = AbstractDungeon.player.drawPile;
            for (int i = 0; i < g.size(); i++) {
                AbstractCard c = g.group.get(i);
                if (c instanceof IYongMingCard) {
                    addCard(c);
                }
            }

            g = AbstractDungeon.player.hand;
            for (int i = 0; i < g.size(); i++) {
                AbstractCard c = g.group.get(i);
                if (c instanceof IYongMingCard) {
                    addCard(c);
                }
            }
        }
    }

    class TurnStartPostDrawListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            final CardGroup g = AbstractDungeon.player.drawPile;
            for (int i = g.size() - 1; i >= 0; i--) {
                AbstractCard c = g.group.get(i);
                if (isYongMingCard(c)) {
                    Utils.addToBotAbstract(() ->{
                        g.moveToHand(c);
                    });
                    Utils.addToBotAbstract(() ->{
                        LinkCardManager.getInstance().tryDrawLinkedCard(c);
                    });
                }
            }

            final CardGroup g2 = AbstractDungeon.player.discardPile;
            for (int i = g2.size() - 1; i >= 0; i--) {
                AbstractCard c = g2.group.get(i);
                if (isYongMingCard(c)) {
                    Utils.addToBotAbstract(() ->{
                        g2.moveToHand(c);
                    });
                    Utils.addToBotAbstract(() ->{
                        LinkCardManager.getInstance().tryDrawLinkedCard(c);
                    });
                }
            }
        }
    }
}