package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.StarBounceEffect;
import com.megacrit.cardcrawl.vfx.WallopEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.*;
import game.duofan.kenshi.variable.ILamageScale;
import game.duofan.kenshi.variable.LamageScaler;

public class XZL_ChunJun extends CustomCard implements IXiaZhiLiuCard, ILamageScale {

    static LamageScaler lamageScaler = new LamageScaler();

    public static final String ID = IDManager.getInstance().getID(XZL_ChunJun.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/xZL/ChunJun_attack.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    AbstractMonster monster;

    public XZL_ChunJun() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        int baseValue = 9;
        this.damage = this.baseDamage = baseValue;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            upgradeDamage(3);
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
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
        monster = m;
        Utils.giveDamage(p, m, damage, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE);
        Utils.addToBotAbstract(() -> {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, false));
        });
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        Utils.determinLiuCardGlowColor(this);
    }

    @Override
    public void xiaZhiLiuEffect(boolean isByQi) {
        AbstractMonster m = this.monster;
        if (m == null) {
            m = Utils.getRandomAliveMonster();
        }

        if (m == null) {
            return;
        }

        final AbstractMonster monster = m;
        AbstractPlayer p = AbstractDungeon.player;
        int d = lamageScaler.value(this);
        Utils.giveDamage(p, monster, d, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE);
        Utils.addToBotAbstract(() -> {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(monster.hb.cX, monster.hb.cY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, false));
        });
        this.addToBot(new VFXAction(new WallopEffect(d, monster.hb.cX, monster.hb.cY)));
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
    public float getScale() {
        return 1.2f;
    }
}
