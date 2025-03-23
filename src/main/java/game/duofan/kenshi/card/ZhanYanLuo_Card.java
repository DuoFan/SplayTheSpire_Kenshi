package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.FleetingField;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.stance.DivinityStanceChangeParticle;
import game.duofan.common.*;
import game.duofan.kenshi.power.ZhanYanLuo;
import game.duofan.kenshi.relic.YiQi;

public class ZhanYanLuo_Card extends CustomCard implements IEventListener {
    public static final String ID = IDManager.getInstance().getID(ZhanYanLuo_Card.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/Strike.png";
    private static final int COST = 1;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final AbstractCard.CardType TYPE = CardType.SKILL;
    private static final AbstractCard.CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final AbstractCard.CardRarity RARITY = CardRarity.RARE;
    private static final AbstractCard.CardTarget TARGET = CardTarget.ENEMY;

    boolean hasYiQi;
    boolean isRegist;

    public ZhanYanLuo_Card() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

        if (AbstractDungeon.player != null) {
            hasYiQi = AbstractDungeon.player.hasRelic(YiQi.ID);
        }

        init();
    }

    void init() {
        String description;

        selfRetain = true;

        if (!hasYiQi) {
            description = CARD_STRINGS.DESCRIPTION;
            exhaust = false;
            FleetingField.fleeting.set(this, true);
            if (!isRegist) {
                isRegist = true;
                EventManager.getInstance().registerToEvent(EventKey.ON_GAIN_YI_QI, this);
            }
        } else {
            exhaust = true;
            FleetingField.fleeting.set(this, false);
            description = CARD_STRINGS.EXTENDED_DESCRIPTION[0];
        }

        this.rawDescription = description;
        this.initializeDescription();
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeBaseCost(0);
            if (!hasYiQi) {
                this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
                this.initializeDescription();
            }
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

        addToBot(new SFXAction("STANCE_ENTER_DIVINITY"));
        Utils.addToBotAbstract(()->{
            for(int i = 0; i < 20; ++i) {
                AbstractDungeon.effectsQueue.add(new DivinityStanceChangeParticle(Color.SCARLET, m.hb.cX, m.hb.cY));
            }
        });

        if (!hasYiQi) {
            Utils.givePower(p, m, new ZhanYanLuo(m));
        } else {
            Utils.givePower(p, m, new StrengthPower(m, -1));
            Utils.playerGainPower(new StrengthPower(m, 1));
        }
        if (isRegist) {
            EventManager.getInstance().unregisterFromEvent(EventKey.ON_GAIN_YI_QI, this);
        }
    }

    @Override
    public void OnEvent(Object sender, Object e) {
        hasYiQi = true;
        init();
    }
}
