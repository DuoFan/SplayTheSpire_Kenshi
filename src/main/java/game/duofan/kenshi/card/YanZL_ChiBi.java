package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.stance.DivinityStanceChangeParticle;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.ChiBiFreeCardAction;
import game.duofan.kenshi.effect.ColorSanctityEffect;
import game.duofan.kenshi.power.*;

public class YanZL_ChiBi extends CustomCard implements IYanZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(YanZL_ChiBi.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/yanZL/chiBi_power.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.POWER;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;

    public YanZL_ChiBi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = 1;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeBaseCost(1);
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
        Color c1 = new Color(1.0F, 1.0F, 0.1F, 1.0F);

        CardCrawlGame.sound.play("ATTACK_FIRE");
        Utils.addToBotAbstract(()->{
            for(int i = 0; i < 20; ++i) {
                AbstractDungeon.effectsQueue.add(new DivinityStanceChangeParticle(c1, p.hb.cX, p.hb.cY));
            }
        });

        AbstractDungeon.effectsQueue.add(new BorderFlashEffect(c1));
        AbstractDungeon.effectsQueue.add(new BorderLongFlashEffect(new Color(1.0F, 0.4F, 0.1F, 1.0F)));
        Utils.gainPower(p, new ChiBi(p, magicNumber));
    }

    @Override
    public void yanZhiLiuEffect() {
        addToBot(new ChiBiFreeCardAction());
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        if (Liu_StateMachine.getInstance().isStateMatch(Liu_StateMachine.StateEnum.YanZhiLiu)
                || ZhuLiuBaiJia.canForceInvokeLiu()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public Liu_StateMachine.StateEnum getLiu() {
        return Liu_StateMachine.StateEnum.YanZhiLiu;
    }

    @Override
    public boolean isInvokeLiuEffectToTop() {
        return false;
    }
}
