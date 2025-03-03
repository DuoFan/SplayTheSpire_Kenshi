package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
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
import game.duofan.kenshi.power.*;

import java.util.UUID;

public class XZL_ShuangJi extends CustomCard implements IXiaZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(XZL_ShuangJi.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    static YangZhiRen yangZR;
    static YinZhiRen yinZR;
    static YangZhiRen yangZRUp;
    static YinZhiRen yinZRUp;

    float previewTime;

    UUID selfYangZRUUid;
    UUID selfYinZRUUID;

    public XZL_ShuangJi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        exhaust = true;
        previewTime = 2;
        if (!upgraded) {
            if (yangZR == null) {
                yangZR = new YangZhiRen();
            }
            if (yinZR == null) {
                yinZR = new YinZhiRen();
            }
            cardsToPreview = yangZR;
        } else {
            if (yangZRUp == null) {
                yangZRUp = new YangZhiRen();
                yangZRUp.upgrade();
            }
            if (yinZRUp == null) {
                yinZRUp = new YinZhiRen();
                yinZRUp.upgrade();
            }
            cardsToPreview = yangZRUp;
        }
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            this.initializeDescription();
            previewTime = 0;

            if (yangZRUp == null) {
                yangZRUp = new YangZhiRen();
                yangZRUp.upgrade();
            }
            if (yinZRUp == null) {
                yinZRUp = new YinZhiRen();
                yinZRUp.upgrade();
            }
        }
    }

    @Override
    public void update() {
        super.update();
        previewTime -= 0.02f;
        if (previewTime < 0) {
            previewTime = 2;
            if (!upgraded) {
                cardsToPreview = (cardsToPreview == yangZR ? yinZR : yangZR);
            } else {
                cardsToPreview = (cardsToPreview == yangZRUp ? yinZRUp : yangZRUp);
            }
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
        AbstractCard selfYangZR = (upgraded ? yangZRUp : yangZR).makeStatEquivalentCopy();
        AbstractCard selfYinZR = (upgraded ? yinZRUp : yinZR).makeStatEquivalentCopy();
        selfYangZRUUid = selfYangZR.uuid;
        selfYinZRUUID = selfYinZR.uuid;
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(selfYangZR, true, true));
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(selfYinZR, true, true));
    }

    @Override
    public void xiaZhiLiuEffect(boolean isByQi) {
        AbstractPlayer p = AbstractDungeon.player;

        tryUpgradeCardWithUUIDInGroup(selfYangZRUUid, p.hand);
        tryUpgradeCardWithUUIDInGroup(selfYangZRUUid, p.drawPile);
        tryUpgradeCardWithUUIDInGroup(selfYangZRUUid, p.discardPile);

        tryUpgradeCardWithUUIDInGroup(selfYinZRUUID, p.hand);
        tryUpgradeCardWithUUIDInGroup(selfYinZRUUID, p.drawPile);
        tryUpgradeCardWithUUIDInGroup(selfYinZRUUID, p.discardPile);
    }

    void tryUpgradeCardWithUUIDInGroup(UUID uuid, CardGroup g) {

        System.out.println("-------------------");

        if (uuid == null || g == null) {
            return;
        }

        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.group.get(i);
            if (c.uuid.equals(uuid)) {
                c.upgrade();
            }
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        if (Liu_StateMachine.getInstance().isStateMatch(Liu_StateMachine.StateEnum.XiaZhiLiu)
                || ZhuLiuBaiJia.canForceInvokeLiu()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public Liu_StateMachine.StateEnum getLiu() {
        return Liu_StateMachine.StateEnum.XiaZhiLiu;
    }

    @Override
    public boolean isInvokeLiuEffectToTop() {
        return false;
    }
}
