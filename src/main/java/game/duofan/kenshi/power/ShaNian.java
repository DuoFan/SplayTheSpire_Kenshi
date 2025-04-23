package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.watcher.EndTurnDeathPower;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import game.duofan.common.*;

import java.util.ArrayList;

public class ShaNian extends AbstractPower {

    static final String POWER_ID = IDManager.getInstance().getID(ShaNian.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean triggerEffect1;
    boolean triggerEffect2;
    boolean triggerEffect3;
    boolean isDeathNextTurn;
    boolean isDeathThisTurn;

    public ShaNian(AbstractCreature owner, int amount) {
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
        checkTrigger();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        checkTrigger();
    }

    public void updateDescription() {
        if (!triggerEffect1) {
            this.description = String.format(DESCRIPTIONS[0]);
        } else if (!triggerEffect2) {
            this.description = String.format(DESCRIPTIONS[1]);
        } else if (!triggerEffect3) {
            this.description = String.format(DESCRIPTIONS[2]);
        } else if (!isDeathNextTurn) {
            this.description = String.format(DESCRIPTIONS[3]);
        } else if(isDeathThisTurn) {
            this.description = String.format(DESCRIPTIONS[4]);
        }
    }

    void checkTrigger() {
        if (amount >= 5 && !triggerEffect1) {
            triggerEffect1 = true;
            effect1();
        }

        if (amount >= 10 && !triggerEffect2) {
            triggerEffect2 = true;
            effect2();
        }

        if (amount >= 15 && !triggerEffect3) {
            triggerEffect3 = true;
        }

        updateDescription();
    }

    void effect1() {
        this.flash();

        ArrayList<AbstractCard> cards = new ArrayList<>();
        ArrayList<CardGroup> groups = new ArrayList<>();

        tryFillUnAttackCards(cards, groups, AbstractDungeon.player.drawPile);
        tryFillUnAttackCards(cards, groups, AbstractDungeon.player.discardPile);
        tryFillUnAttackCards(cards, groups, AbstractDungeon.player.hand);

        for (int i = 0; i < cards.size(); i++) {
            AbstractCard c = cards.get(i);
            CardGroup g = groups.get(i);
            if (c != null) {
                addToTop(new ExhaustSpecificCardAction(c, g));
            }
        }
    }

    void effect2() {
        this.flash();
        Utils.playerGainPower(new StrengthPower(AbstractDungeon.player, 2));
    }

    void deathNextTurn() {
        flash();
        isDeathNextTurn = true;
        this.addToBot(new SkipEnemiesTurnAction());

        updateDescription();
    }

    void endTurnDeath() {
        this.flash();
        this.addToBot(new VFXAction(new LightningEffect(this.owner.hb.cX, this.owner.hb.cY)));
        this.addToBot(new LoseHPAction(this.owner, this.owner, 99999));
    }

    void tryFillUnAttackCards(ArrayList<AbstractCard> cards, ArrayList<CardGroup> groups, CardGroup g) {
        int index = 0;
        int size = g.size();
        while (index < size) {
            AbstractCard c = g.group.get(index);
            if (c.type != AbstractCard.CardType.ATTACK) {
                cards.add(c);
                groups.add(g);
            }
            index++;
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if (triggerEffect3) {
            if (!isDeathNextTurn) {
                deathNextTurn();
            }
            if(isDeathThisTurn){
                endTurnDeath();
            }
        }
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();
        if(isDeathNextTurn){
            isDeathThisTurn = true;
            updateDescription();
        }
    }
}
