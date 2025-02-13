package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.DuanZaoAction;
import game.duofan.kenshi.action.PickUpCardToDuanZaoAction;
import game.duofan.kenshi.power.*;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.FleetingField;

public class DZL_RongHui extends CustomCard implements IDuanZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(DZL_RongHui.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    AbstractCard targetCard;

    public DZL_RongHui() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        exhaust = true;
        isEthereal = true;
        magicNumber = baseMagicNumber = 2;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
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
        addToBot(new PickUpCardToDuanZaoAction((c) -> {
            if (c != null) {
                targetCard = c;
                Utils.addToBotAbstract(() -> {
                    applyToTargetCard();
                });
            }
        }, magicNumber));
    }

    void applyToTargetCard() {
        if (targetCard != null) {
            if (!targetCard.exhaust) {
                targetCard.exhaust = true;
                targetCard.rawDescription += " NL 消耗 ";
            }
            if (!targetCard.isEthereal) {
                targetCard.isEthereal = true;
                targetCard.rawDescription += " NL 虚无 ";
            }

            System.out.println(targetCard.selfRetain + ":" + targetCard.retain);

            if (targetCard.selfRetain || retain) {
                targetCard.selfRetain = false;
                targetCard.retain = false;
                targetCard.rawDescription = targetCard.rawDescription.replace("保留", "");
            }

            if(upgraded){
                if(FleetingField.fleeting.get(targetCard).equals(false)){
                    FleetingField.fleeting.set(targetCard, true);
                    targetCard.rawDescription += " NL 即逝 ";
                }
            }

            if (targetCard instanceof IUpdateDescription) {
                ((IUpdateDescription) targetCard).updateDescription();
            } else {
                targetCard.initializeDescription();
            }
        }
    }

    @Override
    public void duanZhiLiuEffect() {
        if (targetCard != null) {
            addToBot(new DuanZaoAction(targetCard, 1));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        if (Liu_StateMachine.getInstance().isStateMatch(Liu_StateMachine.StateEnum.DuanZhiLiu)) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }
}
