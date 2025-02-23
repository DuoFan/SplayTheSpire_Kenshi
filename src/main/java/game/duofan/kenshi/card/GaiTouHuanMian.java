package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Const;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.*;

import java.util.ArrayList;
import java.util.Iterator;

public class GaiTouHuanMian extends CustomCard {

    public static final String ID = IDManager.getInstance().getID(GaiTouHuanMian.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 0;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;

    public GaiTouHuanMian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        exhaust = true;
        this.isInnate = true;
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

        Utils.addToBotAbstract(() ->{
            int count = AbstractDungeon.player.hand.size();
            count = Math.max(count,0);

            Iterator var1 = AbstractDungeon.player.hand.group.iterator();
            while(var1.hasNext()) {
                AbstractCard c = (AbstractCard)var1.next();
                this.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
            }

            addLiuCards(count);
        });
    }

    void addLiuCards(int amount){
        ArrayList cards = Utils.getCardsFromLiu(Liu_StateMachine.StateEnum.All.getValue());

        for (int i = 0; i < amount; i++) {
            AbstractCard card = Utils.getRandomCardsFromList(cards,true);

            if(card == null){
                break;
            }

            if (upgraded) {
                card.upgrade();
            }

            Utils.makeTempCardInHand(card,1);
        }
    }
}
