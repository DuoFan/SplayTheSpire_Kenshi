package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.BaoLuoWanXiangAction;
import game.duofan.kenshi.power.*;

import java.util.ArrayList;
import java.util.Iterator;

public class BaoLuoWanXiang extends CustomCard {

    public static final String ID = IDManager.getInstance().getID(BaoLuoWanXiang.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    int capacity;

    CardGroup g;

    int CAPACITY = 3;
    int UPGRADED_CAPACITY = 4;

    public BaoLuoWanXiang() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        selfRetain = true;
        magicNumber = baseMagicNumber = CAPACITY;
        capacity = CAPACITY;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeMagicNumber(UPGRADED_CAPACITY - CAPACITY);
            capacity = UPGRADED_CAPACITY;
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        super.triggerOnEndOfTurnForPlayingCard();

        if (g != null && g.size() >= capacity) {
            return;
        }

        CardGroup hand = AbstractDungeon.player.hand;
        if (hand == null) {
            return;
        }
        int index = hand.group.indexOf(this);
        if (index == -1 || index == hand.group.size() - 1) {
            return;
        }
        AbstractCard rightCard = hand.group.get(index + 1);

        if (g == null) {
            g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        }
        g.addToBottom(rightCard);
        rightCard.targetDrawScale = 0.3f;

        Utils.addToBotAbstract(() -> {
            hand.removeCard(rightCard);
        });
        Utils.addToBotAbstract(() -> {
            AbstractDungeon.player.discardPile.removeCard(rightCard);
        });
        Utils.addToBotAbstract(() -> {
            AbstractDungeon.player.drawPile.removeCard(rightCard);
        });
    }

    private static final float[] CARD_OFFSETS_X = new float[]{-50, 50, -50, 50}; // 子卡牌横向偏移
    private static final float[] CARD_OFFSETS_Y = new float[]{80, 80, 20, 20};
    private static final float CARD_SCALE = 0.3f;     // 子卡牌缩放比例

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (g != null && !g.isEmpty()) {
            // 获取父卡牌的屏幕位置
            float parentX = this.current_x;
            float parentY = this.current_y;

            // 遍历所有子卡牌
            for (int i = 0; i < g.size(); i++) {
                AbstractCard childCard = g.getNCardFromTop(i);

                // 设置子卡牌位置（相对父卡偏移）
                childCard.target_x = parentX + CARD_OFFSETS_X[i % CARD_OFFSETS_X.length] * drawScale;
                childCard.target_y = parentY + CARD_OFFSETS_Y[i % CARD_OFFSETS_Y.length] * drawScale;

                // 固定缩放比例
                childCard.targetDrawScale = drawScale * CARD_SCALE;
                childCard.setAngle(angle);

                // 禁用交互区域
                childCard.hb.move(0, 0); // 隐藏点击区域

                // 渲染子卡牌（需要复制原渲染逻辑）
                childCard.render(sb);
            }
        }
    }

    @Override
    public void update() {
        super.update();
        // 确保子卡牌位置实时跟随
        if (g != null) {
            for (int i = 0; i < g.size(); i++) {
                AbstractCard childCard = g.getNCardFromTop(i);
                childCard.update();
            }
        }
    }

    @Override
    public void triggerOnExhaust() {
        super.triggerOnExhaust();
        if (g != null) {
            for (int i = 0; i < g.size(); i++) {
                AbstractCard c = g.getNCardFromTop(i);
                if(c != null){
                    addToBot(new ExhaustSpecificCardAction(c, g));
                }
            }
            g = null;
        }
    }

    /**
     * 当卡牌被使用时，调用这个方法。
     *
     * @param p 你的玩家实体类。
     * @param m 指向的怪物类。（无指向时为null，包括攻击所有敌人时）
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new BaoLuoWanXiangAction(g));
    }
}
