package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.*;
import game.duofan.kenshi.action.PickUpCardToLinkAction;
import game.duofan.kenshi.power.*;

public class FeiXing extends CustomCard {

    public static final String ID = IDManager.getInstance().getID(FeiXing.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;

    public FeiXing() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = 1;
        if (upgraded) {
            ShanShuoCardManager.getInstance().addCard(this);
        }
    }

    public void updateDescription() {
        Utils.updateLinkCard_Description(this);
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
            updateDescription();
            ShanShuoCardManager.getInstance().addCard(this);
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
        addToBot(new DrawCardAction(1));

        if (LinkCardManager.getInstance().findLinkedCard(this) == null) {
            addToBot(new PickUpCardToLinkAction(this));
        }
        setToExchange();
    }

    @Override
    public void triggerOnExhaust() {
        super.triggerOnExhaust();
        LinkCardManager.getInstance().breakLink(this);
        if (upgraded) {
            ShanShuoCardManager.getInstance().removeCard(this);
        }
    }

    public void setToExchange() {
        LinkCardManager.getInstance().forceSetToExchange(this);
        updateDescription();
    }

    @Override
    public AbstractCard makeCopy() {
        AbstractCard c = super.makeCopy();
        c.rawDescription = rawDescription;
        c.initializeDescription();
        return c;
    }
}
