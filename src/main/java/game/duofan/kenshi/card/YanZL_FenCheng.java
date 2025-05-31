package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.FenChengAction;
import game.duofan.kenshi.action.WaitForFenChengAction;
import game.duofan.kenshi.power.*;

public class YanZL_FenCheng extends CustomCard implements IYanZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(YanZL_FenCheng.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/yanZL/fenCheng_attack.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    AbstractMonster targetMonster;

    public YanZL_FenCheng() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = 2;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeMagicNumber(2);
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void update() {
        super.update();
        if (Utils.canInvokeLiuEffect(this)) {
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
        addToBot(new WaitForFenChengAction());
    }

    @Override
    public void yanZhiLiuEffect() {
        AbstractMonster m = targetMonster;
        if(m == null){
            m = Utils.getRandomAliveMonster();
        }
        if(m == null){
            return;
        }
        Utils.givePower(AbstractDungeon.player, m, new RongRong(m, magicNumber));
    }

    @Override
    public void onMoveToDiscard() {
        super.onMoveToDiscard();
        targetMonster = null;
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        Utils.determinLiuCardGlowColor(this);
    }

    @Override
    public Liu_StateMachine.StateEnum getLiu() {
        return Liu_StateMachine.StateEnum.YanZhiLiu;
    }

    @Override
    public boolean isInvokeLiuEffectToTop() {
        return true;
    }
}
