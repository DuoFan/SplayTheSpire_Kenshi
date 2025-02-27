package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import game.duofan.common.*;
import game.duofan.kenshi.card.JingTingXueYe_Card;

import java.util.ArrayList;

public class JingTingXueYe extends AbstractPower {

    static final String POWER_ID = IDManager.getInstance().getID(JingTingXueYe.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JingTingXueYe(AbstractCreature owner, int amount) {
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
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        this.updateDescription();
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], stasticsAttackCardPlayedInTurn());
    }

    int stasticsAttackCardPlayedInTurn() {
        ArrayList<AbstractCard> cards = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        int d = 0;
        for (int i = 0; i < cards.size(); i++) {
            AbstractCard c = cards.get(i);
            if (c.type == AbstractCard.CardType.ATTACK) {
                d++;
            }
        }
        return d;
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);
        if (card.type == AbstractCard.CardType.ATTACK) {
            updateDescription();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        int attackAmount = stasticsAttackCardPlayedInTurn();
        if (attackAmount < JingTingXueYe_Card.attackLimit) {
            flash();
            amount--;
            if(amount == 0){
                Utils.playRemovePower(POWER_ID);
            }
            this.addToBot(new SkipEnemiesTurnAction());
        }
    }
}
