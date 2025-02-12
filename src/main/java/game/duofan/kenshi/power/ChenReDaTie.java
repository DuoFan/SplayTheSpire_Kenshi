package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;

import java.util.ArrayList;

public class ChenReDaTie extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(ChenReDaTie.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;


    ArrayList<AbstractCard> cards;

    public ChenReDaTie(AbstractCreature owner,int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = amount;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();

        effect();
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], amount);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        super.onCardDraw(card);
        if (card instanceof IDuanZhiLiuCard && card.costForTurn > 0) {
            cards.add(card);
            card.setCostForTurn(card.costForTurn - 1);
        }
    }

    protected void effect() {
        cards = new ArrayList<>();
        for (int i = 0; i < AbstractDungeon.player.hand.size(); i++) {
            AbstractCard c = AbstractDungeon.player.hand.group.get(i);
            if (c instanceof IDuanZhiLiuCard && c.costForTurn > 0) {
                cards.add(c);
                c.setCostForTurn(c.costForTurn - 1);
            }
        }
    }

    void restore() {
        for (int i = 0; i < cards.size(); i++) {
            AbstractCard c = cards.get(i);
            c.setCostForTurn(c.costForTurn + 1);
        }
        cards = null;
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card instanceof IDuanZhiLiuCard && !card.purgeOnUse && this.amount > 0) {
            this.flash();
            --this.amount;
            if (this.amount == 0) {
                restore();
                this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if(isPlayer){
            restore();
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }
}