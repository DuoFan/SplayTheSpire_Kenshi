package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.VerticalAuraEffect;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.*;

public class XZL_ZiXiaZhenQi extends CustomCard implements IXiaZhiLiuCard {
    public static final String ID = IDManager.getInstance().getID(XZL_ZiXiaZhenQi.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final AbstractCard.CardType TYPE = CardType.POWER;
    private static final AbstractCard.CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final AbstractCard.CardRarity RARITY = CardRarity.UNCOMMON;
    private static final AbstractCard.CardTarget TARGET = AbstractCard.CardTarget.SELF;

    public XZL_ZiXiaZhenQi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = 1;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeMagicNumber(1);
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
        addToBot(new SFXAction("STANCE_ENTER_CALM"));
        this.addToBot(new VFXAction(p, new BorderLongFlashEffect(Color.PURPLE), 0.0F, true));

        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new ZiXiaZhenQi(p, magicNumber))
        );
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
        Utils.playerGainQi(magicNumber);
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
