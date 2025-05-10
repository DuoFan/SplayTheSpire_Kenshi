package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.stance.DivinityStanceChangeParticle;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.*;

public class XZL_FengMo extends CustomCard implements IXiaZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(XZL_FengMo.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    AbstractMonster targetMonster;

    public XZL_FengMo() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = 5;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级
            upgradeMagicNumber(2);
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

        addToBot(new SFXAction("STANCE_ENTER_DIVINITY"));
        Utils.addToBotAbstract(() -> {
            for (int i = 0; i < 20; ++i) {
                AbstractDungeon.effectsQueue.add(new DivinityStanceChangeParticle(Color.DARK_GRAY, m.hb.cX, m.hb.cY));
            }
        });

        targetMonster = m;
        Utils.givePower(p, m, new WeakPower(m, magicNumber, false));
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
            AbstractPlayer p = AbstractDungeon.player;
            AbstractPower weakP = targetMonster.getPower(WeakPower.POWER_ID);
            if(weakP != null && weakP.amount >= 10){
                int hp = targetMonster.maxHealth;
                if (hp > 0) {
                    int damage = (int) (hp * 0.5f);
                    if (damage > 0) {
                        Utils.giveDamageTop(p, targetMonster, damage, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                    }
                }
            }
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
}
