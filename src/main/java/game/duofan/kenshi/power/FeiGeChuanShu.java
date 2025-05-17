package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.BaiHongGuanRiAction;
import game.duofan.kenshi.action.YanJieAction;
import game.duofan.kenshi.card.YanZL_FenCheng;
import game.duofan.kenshi.card.YanZL_YanJie;

import java.util.ArrayList;

public class FeiGeChuanShu extends AbstractPower {
    static int idIndex;

    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(FeiGeChuanShu.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    AbstractCard card1;
    AbstractCard card2;

    public FeiGeChuanShu(AbstractCreature owner, AbstractCard card1,AbstractCard card2) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.card1 = card1;
        this.card2 = card2;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        updateDescription();
    }

    public void updateDescription() {
        String description = DESCRIPTIONS[0];
        if(card2 != null){
            description = DESCRIPTIONS[1];
        }
        description = description.replace("[NAME1]", card1.name);
        if (card2 != null){
            description = description.replace("[NAME2]",card2.name);
        }
        this.description = description;
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();

        Utils.addToBotAbstract(()->{
            TryDrawCard(card1);
        });

        Utils.addToBotAbstract(()->{
            TryDrawCard(card2);
        });

        flash();

        Utils.playRemovePowerTop(ID);
    }

    void TryDrawCard(AbstractCard c){
        if(c == null){
            return;
        }

        CardGroup g = AbstractDungeon.player.drawPile;
        if(!g.contains(c)){
            return;
        }

        g.removeCard(c);
        g.addToTop(c);
        AbstractDungeon.actionManager.addToTop(new DrawCardAction(1));
    }
}