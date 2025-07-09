package game.duofan.kenshi.card;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import game.duofan.common.*;

public class XueLu_Card extends CustomCard {

    public static final String ID = IDManager.getInstance().getID(XueLu_Card.class);
    private static final CardStrings CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID); // 从游戏系统读取本地化资源
    private static final String NAME = CARD_STRINGS.NAME; // 读取本地化的名字
    private static final String IMG_PATH = "img/cards/XueLu_power.png";
    private static final int COST = -2;
    private static final String DESCRIPTION = CARD_STRINGS.DESCRIPTION; // 读取本地化的描述
    private static final CardType TYPE = CardType.POWER;
    private static final CardColor COLOR = Const.KENSHI_CARD_COLOR;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    static int heal = 3;
    public XueLu_Card() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = 1;
        isEthereal = true;
    }

    @Override
    public void upgrade() { // 升级调用的方法
        if (!this.upgraded) {
            this.upgradeName(); // 卡牌名字变为绿色并添加“+”，且标为升级过的卡牌，之后不能再升级。
            upgradeMagicNumber(1);
            this.rawDescription = CARD_STRINGS.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public void update() {
        super.update();

        MapRoomNode n = AbstractDungeon.currMapNode;
        if(n == null || n.getRoom() == null || n.getRoom().monsters == null){
            cost = -2;
            costForTurn = -2;
            this.isCostModifiedForTurn = false;
            return;
        }

        MonsterGroup monsterGroup = n.getRoom().monsters;

        int c = -magicNumber;
        for (int i = 0; i < monsterGroup.monsters.size(); i++) {
            AbstractMonster m = monsterGroup.monsters.get(i);
            if(Utils.isKilled(m) || m.isDeadOrEscaped()){
                continue;
            }
            c++;
        }

        if(c < 0){
            c = 0;
        }

        this.cost = c;
        this.costForTurn = c;
        this.isCostModifiedForTurn = false;
    }

    /**
     * 当卡牌被使用时，调用这个方法。
     *
     * @param p 你的玩家实体类。
     * @param m 指向的怪物类。（无指向时为null，包括攻击所有敌人时）
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new SFXAction("STANCE_ENTER_WRATH"));
        Color c1 = Color.SCARLET;
        this.addToBot(new VFXAction(p, new BorderLongFlashEffect(c1), 0.0F, true));
        Utils.playerGainPower(new game.duofan.kenshi.power.XueLu(p, heal));
    }
}
