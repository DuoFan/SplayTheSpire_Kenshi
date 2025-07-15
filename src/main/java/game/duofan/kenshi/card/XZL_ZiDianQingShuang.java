package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.ClashEffect;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.IXiaZhiLiuCard;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.ZhuLiuBaiJia;

public class XZL_ZiDianQingShuang extends CustomCard implements IXiaZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(XZL_ZiDianQingShuang.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/xZL/ZiDianQingShuang_attack.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    int attackDamage;
    AbstractMonster targetMonster;

    public XZL_ZiDianQingShuang() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = 2;
        updateDamage();
    }

    @Override
    public void update() {
        super.update();
        updateDamage();
    }

    void updateDamage() {
        // Should generally just be the above.
        AbstractPlayer p = AbstractDungeon.player;
        int d = 0;
        if (p != null && p.hand != null) {
            CardGroup hand = p.hand;
            int attackCount = 0;
            for (int i = 0; i < hand.size(); i++) {
                AbstractCard c = hand.group.get(i);
                if (c.type == AbstractCard.CardType.ATTACK) {
                    attackCount++;
                }
            }
            d = attackCount * magicNumber;
        }
        baseDamage = d;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeMagicNumber(1);
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
        targetMonster = m;
        attackDamage = damage;
        if (attackDamage > 0) {

            if (m != null) {
                this.addToBot(new VFXAction(new ClashEffect(m.hb.cX, m.hb.cY), 0.1F));
            }
            Utils.giveDamage(p, m, attackDamage, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE);
        }
    }

    @Override
    public void onMoveToDiscard() {
        super.onMoveToDiscard();
        attackDamage = 0;
        targetMonster = null;
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        attackDamage = 0;
        targetMonster = null;
        updateDamage();
    }

    @Override
    public Liu_StateMachine.StateEnum getLiu() {
        return Liu_StateMachine.StateEnum.XiaZhiLiu;
    }

    @Override
    public boolean isInvokeLiuEffectToTop() {
        return false;
    }

    @Override
    public void triggerOnGlowCheck() {
        super.triggerOnGlowCheck();
        Utils.determinLiuCardGlowColor(this);
    }

    @Override
    public void xiaZhiLiuEffect(boolean isByQi) {
        AbstractMonster m = targetMonster;
        if (m == null) {
            m = Utils.getRandomAliveMonster();
        }
        if (m == null) {
            return;
        }
        Utils.givePower(AbstractDungeon.player, m,
                new VulnerablePower(m, 1, false));
    }
}
