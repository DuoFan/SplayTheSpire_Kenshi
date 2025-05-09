package game.duofan.kenshi.card;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import basemod.interfaces.OnPlayerDamagedSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.VerticalAuraEffect;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.JiShuiSanQianAction;
import game.duofan.kenshi.action.QiuYeLianJianAction;
import game.duofan.kenshi.action.SuShaAction;
import game.duofan.kenshi.power.*;

public class FZL_SuSha extends CustomCard implements IFengZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(FZL_SuSha.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/fZL/suSha_skill.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    AbstractMonster targetMonster;
    public AbstractCard targetCard;

    public FZL_SuSha() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        exhaust = true;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级
            exhaust = false;
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void update() {
        super.update();
        if (Liu_StateMachine.getInstance().getLiu() == Liu_StateMachine.StateEnum.FengZhiLiu) {
            target = CardTarget.ENEMY;
        } else {
            target = CardTarget.NONE;
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
        targetMonster = m;
        addToBot(new SuShaAction(this));
        /*Utils.addToBotAbstract(() -> {
            if (targetCard != null) {
                Utils.playerGainPowerTop(new SuSha(p, targetCard, magicNumber));
            }
        });*/
        this.addToBot(new SFXAction("ATTACK_FIRE"));
        this.addToBot(new VFXAction(p, new VerticalAuraEffect(Color.RED, p.hb.cX, p.hb.cY), 0.33F));
    }

    @Override
    public void fengZhiLiuEffect() {
        Utils.playerGainPower(new PoBaiGongJi(AbstractDungeon.player, 1));
        /*if (targetMonster != null) {
            Utils.givePower(AbstractDungeon.player, targetMonster, new VulnerablePower(targetMonster, 1, false));
        }*/
    }

    @Override
    public void onMoveToDiscard() {
        super.onMoveToDiscard();
        targetMonster = null;
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        if (Liu_StateMachine.getInstance().isStateMatch(Liu_StateMachine.StateEnum.FengZhiLiu)
                || ZhuLiuBaiJia.canForceInvokeLiu()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public Liu_StateMachine.StateEnum getLiu() {
        return Liu_StateMachine.StateEnum.FengZhiLiu;
    }

    @Override
    public boolean isInvokeLiuEffectToTop() {
        return false;
    }
}
