package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.*;

import java.util.ArrayList;

public class YanZL_LuoXuanYan extends CustomCard implements IYanZhiLiuCard {

    public static final String ID = IDManager.getInstance().getID(YanZL_LuoXuanYan.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/yanZL/luoXuanYan_attack.png";
    private static final int COST = 2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public YanZL_LuoXuanYan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        damage = baseDamage = 16;
        magicNumber = baseMagicNumber = 1;
        BaoYanCardManager.getInstance().addCard(this);
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeDamage(4);
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
        Utils.giveBaoYanDamage(p, m, damage, DamageInfo.DamageType.NORMAL);
        Utils.addToBotAbstract(() ->{
            AbstractPower rongrong = m.getPower(RongRong.POWER_ID);
            if (rongrong != null && rongrong.amount > 0) {
                ArrayList<AbstractMonster> monsters = Utils.getAllAliveMonsters();
                if (monsters != null) {
                    for (int i = 0; i < monsters.size(); i++) {
                        AbstractMonster _m = monsters.get(i);
                        if (m.equals(_m)) {
                            continue;
                        }
                        Utils.giveBaoYanDamage(p, _m, rongrong.amount, DamageInfo.DamageType.NORMAL);
                    }
                }
            }
        });
    }

    @Override
    public void yanZhiLiuEffect() {
        ArrayList<AbstractMonster> monsters = Utils.getAllAliveMonsters();
        AbstractPlayer p = AbstractDungeon.player;
        for (int i = 0; i < monsters.size(); i++) {
            AbstractMonster m = monsters.get(i);
            Utils.givePower(p, m, new RongRong(m, magicNumber));
        }
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
        return false;
    }
}
