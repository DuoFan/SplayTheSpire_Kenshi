package game.duofan.kenshi.power;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;
import game.duofan.common.Utils;

public class ShanShuoCardManager extends TagCardManager {
    private static ShanShuoCardManager instance;

    public static ShanShuoCardManager getInstance() {
        if (instance == null) {
            instance = new ShanShuoCardManager();
        }
        return instance;
    }

    int turn;

    @Override
    protected AbstractCard.CardTags getTag() {
        return SHAN_SHUO;
    }

    public ShanShuoCardManager() {
        super();
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_BATTLE_START, new BattleStartListener());
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_TURN_START_POST_DRAW, new TurnStartPostDrawListener());
    }


    @SpireEnum
    public static AbstractCard.CardTags SHAN_SHUO;


    class BattleStartListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            turn = 0;
        }
    }

    class TurnStartPostDrawListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            turn++;

            if (turn == 2) {
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
}