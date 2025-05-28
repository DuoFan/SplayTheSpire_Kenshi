package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.SanctityEffect;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.DrawCardByClassAction;
import game.duofan.kenshi.action.ICardFilter;
import game.duofan.kenshi.power.*;

public class XZL_CaiCheQuMing extends CustomCard implements IXiaZhiLiuCard {
    public static final String ID = IDManager.getInstance().getID(XZL_CaiCheQuMing.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/xZL/CaiCheQuMing_skill.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final AbstractCard.CardType TYPE = CardType.SKILL;
    private static final AbstractCard.CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final AbstractCard.CardRarity RARITY = CardRarity.COMMON;
    private static final AbstractCard.CardTarget TARGET = CardTarget.SELF;

    Liu_StateMachine.StateEnum playingLiu;

    public XZL_CaiCheQuMing() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        super.onPlayCard(c, m);
        playingLiu = Liu_StateMachine.getInstance().getLiu();
    }

    /**
     * 当卡牌被使用时，调用这个方法。
     *
     * @param p 你的玩家实体类。
     * @param m 指向的怪物类。（无指向时为null，包括攻击所有敌人时）
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToTop(new VFXAction(new SanctityEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY)));
        this.addToTop(new SFXAction("HEAL_1"));
        this.addToTop(new VFXAction(new BorderFlashEffect(Color.SKY, true), 0.1F));

        if (playingLiu != Liu_StateMachine.StateEnum.None) {
            Utils.playerDrawCardByFilterAction(1, (c) -> {
                return Utils.getLiuFromCard(c) == playingLiu;
            });
        }

        if (upgraded) {
            Utils.playerGainQi(1);
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        Liu_StateMachine.StateEnum curLiu = Liu_StateMachine.getInstance().getLiu();
        if (curLiu == Liu_StateMachine.StateEnum.None) {
            return;
        }

        if (Utils.calculateRefreshDiscardPileForDraw((c) -> {
            return Utils.getLiuFromCard(c) == Liu_StateMachine.getInstance().getLiu();
        }) > 0) {
            if (Utils.canInvokeLiuEffect(this)) {
                this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
            } else {
                this.glowColor = Color.PURPLE.cpy();
            }
        }
    }

    @Override
    public void xiaZhiLiuEffect(boolean isByQi) {
        Utils.returnLastLiuAction();
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
