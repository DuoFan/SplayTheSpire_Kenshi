package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import game.duofan.kenshi.action.BaoFuAction;

public class BaoFu extends CustomCard implements ICardContainer {

    public static final String ID = IDManager.getInstance().getID(BaoFu.class);
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

    public BaoFu() {
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

            Utils.upgradeCardContainer(this);
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
        rightCard.targetDrawScale = CARD_SCALE;

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
        Utils.renderCardContainer(sb, this);
    }

    @Override
    public void update() {
        super.update();
        Utils.updateCardContainer(this);
    }

    @Override
    public void triggerOnExhaust() {
        super.triggerOnExhaust();
        Utils.exhaustCardContainer(this);
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        Utils.glowCheckCardContainer(this);
    }

    /**
     * 当卡牌被使用时，调用这个方法。
     *
     * @param p 你的玩家实体类。
     * @param m 指向的怪物类。（无指向时为null，包括攻击所有敌人时）
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new BaoFuAction(g));
    }

    @Override
    public CardGroup getContainer() {
        return g;
    }

    @Override
    public float[] getCardOffsetsX() {
        return CARD_OFFSETS_X;
    }

    @Override
    public float[] getCardOffsetsY() {
        return CARD_OFFSETS_Y;
    }

    @Override
    public float getCardScale() {
        return CARD_SCALE;
    }
}
