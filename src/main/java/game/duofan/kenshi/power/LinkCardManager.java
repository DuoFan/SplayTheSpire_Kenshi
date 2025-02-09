package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.kenshi.card.SZL_ShanJi;

import java.util.HashMap;

public class LinkCardManager {
    static LinkCardManager instance;

    public static LinkCardManager getInstance() {
        if (instance == null) {
            instance = new LinkCardManager();
        }
        return instance;
    }

    HashMap<AbstractCard, AbstractCard> selfMapLinked;
    HashMap<AbstractCard, AbstractCard> linkedMapSelf;

    public void clear() {
        if (selfMapLinked != null) {
            selfMapLinked.clear();
        }
        if (linkedMapSelf != null) {
            linkedMapSelf.clear();
        }
    }

    public void makeLink(AbstractCard self, AbstractCard linked) {
        if (selfMapLinked == null) {
            selfMapLinked = new HashMap<>();
        }
        selfMapLinked.put(self, linked);

        if (linkedMapSelf == null) {
            linkedMapSelf = new HashMap<>();
        }

        linkedMapSelf.put(linked, self);

        System.out.println(self.name + "连锁了" + linked.name);
    }

    public void breakLink(AbstractCard self) {
        AbstractCard linked = null;
        if (linkedMapSelf != null) {
            linked = findLinkedCard(self);
            linkedMapSelf.remove(linked);
        }

        if (selfMapLinked != null) {
            selfMapLinked.remove(self);
        }

        if(linked != null && self != null){
            System.out.println(self.name + "断开了" + linked.name);
        }
    }


    public AbstractCard findLinkedCard(AbstractCard self) {
        if (selfMapLinked == null) {
            return null;
        }
        return selfMapLinked.get(self);
    }

    AbstractCard findSelfCard(AbstractCard linked) {
        if (linkedMapSelf == null) {
            return null;
        }
        return linkedMapSelf.get(linked);
    }

    public void tryPlaySelfCard(AbstractCard linked) {
        AbstractCard selfCard = findSelfCard(linked);
        if(selfCard != null){
            AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction(selfCard, true, false, true));
        }
    }
}
