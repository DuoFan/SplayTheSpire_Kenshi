package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.PickUpCardToLinkAction;
import game.duofan.kenshi.action.PickUpCardsDoAction;
import game.duofan.kenshi.power.*;

import static game.duofan.common.Const.PREVIEW_OFFSET_X;

public class SZL_KuaiFang extends CustomCard implements IShanZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(SZL_KuaiFang.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    boolean effectable;

    public SZL_KuaiFang() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        block = baseBlock = 5;
        effectable = true;
    }

    public void updateDescription() {
        Utils.updateSZL_Description(this);
    }

    @Override
    public void update() {
        super.update();
        if (this.hb.hovered) {
            if (cardsToPreview == null) {
                LinkCardManager.getInstance().updateHoverPreview(this);
            }
        } else {
            cardsToPreview = null;
        }
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeBlock(3);
            updateDescription();
            this.initializeDescription();
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
        Utils.playerGainBlock(block);

        if (LinkCardManager.getInstance().findLinkedCard(this) == null) {
            addToBot(new PickUpCardToLinkAction(this));
        }
    }

    @Override
    public void triggerOnExhaust() {
        super.triggerOnExhaust();
        LinkCardManager.getInstance().breakLink(this);
    }

    @Override
    public void shanZhiLiuEffect() {
        setToExchange();
    }

    @Override
    public boolean isExchangeAble() {
        return true;
    }

    @Override
    public void setToExchange() {
        effectable = false;
        LinkCardManager.getInstance().forceSetToExchange(this);
        updateDescription();
    }

    @Override
    public boolean effectable() {
        return effectable;
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        if (effectable && (Liu_StateMachine.getInstance().isStateMatch(Liu_StateMachine.StateEnum.ShanZhiLiu)
                || ZhuLiuBaiJia.canForceInvokeLiu())) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        AbstractCard c = super.makeCopy();
        c.rawDescription = rawDescription;
        c.initializeDescription();
        return c;
    }

    @Override
    public Liu_StateMachine.StateEnum getLiu() {
        return Liu_StateMachine.StateEnum.ShanZhiLiu;
    }
}
