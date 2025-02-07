package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.unique.DamagePerAttackPlayedAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.kenshi.action.RepeatAction;
import game.duofan.kenshi.power.IFengZhiLiuCard;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.Shi_StateMachine;

public class FZL_KuangFengJuanYe extends CustomCard implements IFengZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(FZL_KuangFengJuanYe.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public FZL_KuangFengJuanYe() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = 8;
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            this.upgradeDamage(3);
            this.upgradeMagicNumber(1);
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
        this.addToBot(new DamageAction(
                m,new DamageInfo(p,this.damage, DamageInfo.DamageType.NORMAL)
        ));
        Shi_StateMachine.getInstance().addPower(Shi_StateMachine.StateEnum.GongShi,1);
    }

    @Override
    public void FengZhiLiuEffect() {
        addToBot(new DrawCardAction(1));
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        if(Liu_StateMachine.getInstance().isStateMatch(Liu_StateMachine.StateEnum.FengZhiLiu)){
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }
}
