package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.*;

public class YinZhiRen extends CustomCard implements IFengZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(YinZhiRen.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = CardColor.COLORLESS;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    static int _baseDamage = 4;

    public YinZhiRen() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = _baseDamage;
        magicNumber = baseMagicNumber = 1;
        cardsToPreview = new YangZhiRen(true);
    }

    public YinZhiRen(boolean dontPreview) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.damage = this.baseDamage = _baseDamage;
        magicNumber = baseMagicNumber = 1;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        this.upgradeDamage(2);
        ++this.timesUpgraded;
        this.upgraded = true;
        this.name = NAME + "+" + this.timesUpgraded;
        this.initializeTitle();
    }

    @Override
    public boolean canUpgrade() {
        return true;
    }

    /**
     * 当卡牌被使用时，调用这个方法。
     *
     * @param p 你的玩家实体类。
     * @param m 指向的怪物类。（无指向时为null，包括攻击所有敌人时）
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Utils.giveDamage(p, m, damage, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.SLASH_HEAVY);
        Utils.givePower(p, m, new PoBai(m, magicNumber));
        Utils.playerDrawCardByClass(1, YangZhiRen.class);
    }

    @Override
    public void fengZhiLiuEffect() {

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
