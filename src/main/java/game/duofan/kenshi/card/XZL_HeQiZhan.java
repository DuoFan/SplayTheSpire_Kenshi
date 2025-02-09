package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.watcher.FollowUpAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.IFengZhiLiuCard;
import game.duofan.kenshi.power.IXiaZhiLiuCard;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.Shi_StateMachine;

public class XZL_HeQiZhan extends CustomCard implements IXiaZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(XZL_HeQiZhan.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    AbstractMonster monster;

    public XZL_HeQiZhan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        int baseValue = 6;
        this.damage = this.baseDamage = baseValue;
        calculateMagicNumber();
    }

    @Override
    public void update() {
        super.update();
        calculateMagicNumber();
    }

    int calculateMagicNumber(){
        int value = Math.max(damage / 2,0);

        this.magicNumber = this.baseMagicNumber = value;

        return value;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;

            upgradeDamage(2);
            calculateMagicNumber();
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
        this.addToBot(new DamageAction(m, new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL)));
        exhaustOnUseOnce = Liu_StateMachine.getInstance().isStateMatch(Liu_StateMachine.StateEnum.XiaZhiLiu);
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (Liu_StateMachine.getInstance().isStateMatch(Liu_StateMachine.StateEnum.XiaZhiLiu)) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void xiaZhiLiuEffect() {
        if(monster != null){
            this.addToBot(new DamageAction(monster, new DamageInfo(AbstractDungeon.player, calculateMagicNumber(), DamageInfo.DamageType.NORMAL)));
        }
    }
}
