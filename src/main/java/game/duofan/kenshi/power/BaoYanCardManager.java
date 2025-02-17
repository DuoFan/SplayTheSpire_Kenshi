package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;

import java.util.HashSet;

public class BaoYanCardManager implements IEventListener {
    private static BaoYanCardManager instance;
    HashSet<AbstractCard> cards;

    public static BaoYanCardManager getInstance() {
        if (instance == null) {
            instance = new BaoYanCardManager();
        }
        return instance;
    }

    public BaoYanCardManager() {
        cards = new HashSet<>();
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_BATTLE_START, this);
    }

    public void clear() {
        cards.clear();
    }

    public void addCard(AbstractCard c) {
        if (cards.add(c)) {
            System.out.println(c.name + "被设置为爆炎伤害");
        }
    }

    public void removeCard(AbstractCard c) {
        if (cards.remove(c)) {
            System.out.println(c.name + "被移除爆炎伤害");
        }
    }

    public boolean isBaoYanCard(AbstractCard c) {
        return cards.contains(c);
    }

    @Override
    public void OnEvent(Object sender, Object e) {
        clear();
        CardGroup g = AbstractDungeon.player.drawPile;
        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.group.get(i);
            if(c instanceof IBaoYanCard){
                addCard(c);
            }
        }

        g = AbstractDungeon.player.hand;
        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.group.get(i);
            if(c instanceof IBaoYanCard){
                addCard(c);
            }
        }

    }
}