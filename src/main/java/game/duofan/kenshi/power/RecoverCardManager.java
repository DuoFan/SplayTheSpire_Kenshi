package game.duofan.kenshi.power;

import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.FleetingField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;

import java.util.HashSet;
import java.util.Iterator;

public class RecoverCardManager {

    static RecoverCardManager instance;
    private HashSet<AbstractCard> waitToRecoverCards; // 卡牌到节点的映射


    public static RecoverCardManager getInstance() {
        if (instance == null) {
            instance = new RecoverCardManager();
        }
        return instance;
    }

    public RecoverCardManager() {
        waitToRecoverCards = new HashSet<>();
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_BOSS_BATTLE_START, new BossBattleStartListener());
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_CARD_PLAY, new CardPlayListener());
    }

    public void clearCards() {
        waitToRecoverCards.clear();
    }

    public void tryAddCard(AbstractCard c) {

        if (FleetingField.fleeting.get(c).equals(false)) {
            System.out.println(c.name + "不拥有即逝，不可复苏");
            return;
        }

        if (AbstractDungeon.player == null) {
            System.out.println(c.name + "玩家未初始化，不可复苏");
            return;
        }

        AbstractCard origin = StSLib.getMasterDeckEquivalent(c);
        if (origin == null) {
            System.out.println(c.name + "本体不在卡组中，不可复苏");
            return;
        }

        System.out.println(c.name + "被添加到复苏卡堆");

        waitToRecoverCards.add(c);
    }

    class CardPlayListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            if (e instanceof IRecoverCard && e instanceof AbstractCard) {
                RecoverCardManager.getInstance().tryAddCard((AbstractCard) e);
            }
        }
    }

    class BossBattleStartListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            if (waitToRecoverCards.size() <= 0) {
                return;
            }
            if (AbstractDungeon.player == null) {
                System.out.println("玩家未初始化，无法恢复复苏卡堆。");
                waitToRecoverCards.clear();
                return;
            }

            CardGroup deck = AbstractDungeon.player.masterDeck;
            if (deck == null) {
                if (AbstractDungeon.player == null) {
                    System.out.println("卡组未初始化，无法恢复复苏卡堆。");
                    waitToRecoverCards.clear();
                    return;
                }
            }
            CardGroup drawPile = AbstractDungeon.player.drawPile;

            Iterator<AbstractCard> i = waitToRecoverCards.iterator();
            AbstractCard c;
            AbstractCard deckC;
            AbstractCard drawC;
            StringBuilder builder = new StringBuilder();
            builder.append("Boss战复苏卡:");
            while (i.hasNext()) {
                c = i.next();
                deckC = c.makeCopy();
                drawC = c.makeCopy();
                if (c.upgraded) {
                    deckC.upgrade();
                    drawC.upgrade();
                }
                deckC.uuid = c.uuid;
                drawC.uuid = c.uuid;
                deck.addToTop(deckC);
                drawPile.addToTop(drawC);
                builder.append(deckC.name);
                builder.append(",");
            }
            System.out.println(builder);

            waitToRecoverCards.clear();

            drawPile.shuffle(AbstractDungeon.relicRng);
        }
    }
}
