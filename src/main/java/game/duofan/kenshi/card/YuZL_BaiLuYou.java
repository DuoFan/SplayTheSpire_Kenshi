package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.evacipated.cardcrawl.mod.stslib.actions.common.AutoplayCardAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import game.duofan.common.*;
import game.duofan.kenshi.power.*;

public class YuZL_BaiLuYou extends CustomCard implements IYuZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(YuZL_BaiLuYou.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    public YuZL_BaiLuYou() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        block = baseBlock = 8;
        magicNumber = baseMagicNumber = 1;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeBlock(3);
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
        Utils.playerGainBlock(block);
    }

    @Override
    public void yuZhiLiuEffect() {
        Utils.playerGainPower(new DexterityPower(AbstractDungeon.player, magicNumber));
    }

    @Override
    public void triggerOnCardPlayed(AbstractCard cardPlayed) {
        super.triggerOnCardPlayed(cardPlayed);

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return;
        }

        CardGroup drawPile = p.drawPile;
        if (drawPile == null) {
            return;
        }

        if (cardPlayed.type != CardType.SKILL) {
            return;
        }

        if (cardPlayed == this) {
            return;
        }

        boolean canUse = false;

        for (int i = 0; i < drawPile.size(); i++) {
            if (drawPile.group.get(i) == this) {
                canUse = true;
                break;
            }
        }

        if (canUse) {
            Utils.addToBotAbstract(() -> {
                drawPile.removeCard(this);
                addToBot(new SFXAction(AudioKey.BAI_LU_YOU_SOUND));
                AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, null,
                        EnergyPanel.getCurrentEnergy(), true, true));
            });
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        if (Utils.canInvokeLiuEffect(this)) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public Liu_StateMachine.StateEnum getLiu() {
        return Liu_StateMachine.StateEnum.YuZhiLiu;
    }

    @Override
    public boolean isInvokeLiuEffectToTop() {
        return false;
    }
}
