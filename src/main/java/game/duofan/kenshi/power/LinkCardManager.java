package game.duofan.kenshi.power;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.kenshi.card.SZL_ShanJi;

import java.util.ArrayList;
import java.util.HashMap;

public class LinkCardManager {
    static LinkCardManager instance;
    private HashMap<AbstractCard, LinkNode> cardToNodeMap; // 卡牌到节点的映射
    ArrayList<AbstractCard> selfCards;


    public static LinkCardManager getInstance() {
        if (instance == null) {
            instance = new LinkCardManager();
        }
        return instance;
    }

    public LinkCardManager() {
        cardToNodeMap = new HashMap<>();
    }

    /**
     * 清空所有连锁关系
     */
    public void clear() {
        cardToNodeMap.clear();
    }

    /**
     * 检查两张卡牌是否可以建立连锁关系
     */
    public boolean canMakeLink(AbstractCard self, AbstractCard linked) {
        if (self == null || linked == null || self == linked) {
            return false; // 不允许自环或空卡
        }

        LinkNode selfNode = getOrCreateNode(self);
        LinkNode linkedNode = getOrCreateNode(linked);

        // 检查是否构成环路
        LinkNode current = linkedNode;
        while (current != null) {
            if (current == selfNode) {
                return false; // 构成环路
            }
            current = current.linkedNode;
        }

        return true; // 可以建立连锁
    }

    /**
     * 建立连锁关系
     */
    public boolean makeLink(AbstractCard self, AbstractCard linked) {
        if (!canMakeLink(self, linked)) {
            return false; // 无法建立连锁
        }

        LinkNode selfNode = getOrCreateNode(self);
        LinkNode linkedNode = getOrCreateNode(linked);

        selfNode.linkedNode = linkedNode; // 建立链接
        System.out.println(self.name + " 连锁了 " + linked.name);
        return true;
    }

    /**
     * 断开连锁关系
     */
    public void breakLink(AbstractCard self) {
        LinkNode selfNode = cardToNodeMap.get(self);
        if (selfNode == null || selfNode.linkedNode == null) {
            return; // 无连锁关系
        }

        AbstractCard linked = selfNode.linkedNode.selfCard;
        selfNode.linkedNode = null; // 断开链接
        System.out.println(self.name + " 断开了 " + linked.name);
    }

    /**
     * 查找被连锁的卡牌
     */
    public AbstractCard findLinkedCard(AbstractCard self) {
        LinkNode selfNode = cardToNodeMap.get(self);
        if (selfNode == null || selfNode.linkedNode == null) {
            return null; // 无连锁关系
        }
        return selfNode.linkedNode.selfCard;
    }

    /**
     * 查找连锁当前卡牌的卡牌
     */
    public ArrayList<AbstractCard> findSelfCards(AbstractCard linked) {
        if(selfCards == null){
            selfCards = new ArrayList<>();
        }
        selfCards.clear();
        for (LinkNode node : cardToNodeMap.values()) {
            if (node.linkedNode != null && node.linkedNode.selfCard == linked) {
                selfCards.add(node.selfCard);
            }
        }
        return selfCards;
    }

    /**
     * 尝试播放连锁卡牌
     */
    public void tryPlaySelfCard(AbstractCard linked) {
        ArrayList<AbstractCard> selfCards = findSelfCards(linked);
        for (int i = 0; i < selfCards.size(); i++) {
            AbstractCard c = selfCards.get(i);
            AbstractDungeon.player.drawPile.removeCard(c);
            AbstractDungeon.player.discardPile.removeCard(c);
            AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction(c, true, false, true));
        }
    }

    /**
     * 尝试抽取连锁卡牌，并弃置置换卡
     */
    public void tryDrawLinkedCard(AbstractCard self) {
        LinkNode selfNode = cardToNodeMap.get(self);
        if (selfNode == null || !selfNode.isExchange) {
            return;
        }

        AbstractCard linkedCard = findLinkedCard(self);
        if (linkedCard == null) {
            return;
        }

        // 检查连锁卡牌是否在抽牌堆
        if (AbstractDungeon.player.drawPile.contains(linkedCard)) {
            // 抽取连锁卡牌
            AbstractDungeon.player.drawPile.moveToHand(linkedCard, AbstractDungeon.player.drawPile);

            // 弃置当前置换卡
            AbstractDungeon.player.hand.moveToDiscardPile(self);

            System.out.println("抽取连锁卡牌: " + linkedCard.name + "，弃置换卡: " + self.name);
        }
    }

    /**
     * 获取或创建节点
     */
    private LinkNode getOrCreateNode(AbstractCard card) {
        LinkNode node = cardToNodeMap.get(card);
        if (node == null) {
            node = new LinkNode(card);
            cardToNodeMap.put(card, node);
        }
        return node;
    }

    public boolean isSetToExchange(AbstractCard card) {
        LinkNode node = cardToNodeMap.get(card);
        return node != null && node.isExchange;
    }

    public void setToExchange(AbstractCard card) {
        LinkNode node = cardToNodeMap.get(card);

        if(node == null){
            return;
        }

        node.isExchange = true;

        System.out.println(card.name + " 成为了置换牌");
    }

    public void updateHoverPreview(IShanZhiLiuCard szlCard) {
        AbstractCard card = (AbstractCard) szlCard;

        LinkNode node = cardToNodeMap.get(card);
        if (node == null) {
            szlCard.setLinkedCardHoverPreview(null);
            return;
        }

        // 当前卡是连锁发起者，显示被连锁卡
        if (node.linkedNode != null) {
            szlCard.setLinkedCardHoverPreview(node.linkedNode.selfCard.makeCopy());
        }
    }

    public class LinkNode {
        public AbstractCard selfCard; // 当前卡牌
        public boolean isExchange;    // 是否为置换卡
        public LinkNode linkedNode;   // 指向被连锁的卡牌节点

        public LinkNode(AbstractCard selfCard) {
            this.selfCard = selfCard;
            this.isExchange = false;
            this.linkedNode = null;
        }
    }
}