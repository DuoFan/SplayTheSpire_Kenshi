package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.*;
import game.duofan.kenshi.variable.ITargetMonsterGetter;
import game.duofan.kenshi.variable.OverPJAmount;
import game.duofan.kenshi.variable.PJAmount;

public class XZL_JuQue extends CustomCard implements IXiaZhiLiuCard, ITargetMonsterGetter {

    static PJAmount pjAmount = new PJAmount();
    static OverPJAmount overPJAmount = new OverPJAmount();

    public static final String ID = IDManager.getInstance().getID(XZL_JuQue.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    AbstractMonster targetMonster;

    public XZL_JuQue() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        int baseValue = 14;
        this.damage = this.baseDamage = baseValue;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeDamage(3);
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        targetMonster = mo;
    }

    /**
     * 当卡牌被使用时，调用这个方法。
     *
     * @param p 你的玩家实体类。
     * @param m 指向的怪物类。（无指向时为null，包括攻击所有敌人时）
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        targetMonster = m;

        int _damage = pjAmount.value(this);
        if (_damage > 0) {
            Utils.giveDamage(p, m, _damage, DamageInfo.DamageType.NORMAL);
        }

        _damage = overPJAmount.value(this);
        if (_damage > 0) {
            Utils.giveDamage(p, m, _damage, DamageInfo.DamageType.NORMAL);
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
    public void xiaZhiLiuEffect(boolean isByQi) {
        if (targetMonster != null) {
            Utils.givePower(AbstractDungeon.player, targetMonster, new PoJia(targetMonster, 1));
        }
    }

    @Override
    public void onMoveToDiscard() {
        super.onMoveToDiscard();
        targetMonster = null;
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        targetMonster = null;
    }

    @Override
    public Liu_StateMachine.StateEnum getLiu() {
        return Liu_StateMachine.StateEnum.XiaZhiLiu;
    }

    @Override
    public boolean isInvokeLiuEffectToTop() {
        return false;
    }

    @Override
    public AbstractMonster getTargetMonster() {
        return targetMonster;
    }
}
