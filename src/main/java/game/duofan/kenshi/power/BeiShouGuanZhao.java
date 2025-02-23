package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.DrawCardByFilterAction;

public class BeiShouGuanZhao extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(BeiShouGuanZhao.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BeiShouGuanZhao(AbstractCreature owner, int _amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = _amount;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        updateDescription();
    }

    public void updateDescription() {
        String d = DESCRIPTIONS[0].replace("NAME", owner.name);
        this.description = String.format(d, this.amount);
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);
        tryEffect();
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();
        tryEffect();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        amount--;
        if (amount == 0) {
            addToTop(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
        } else {
            tryEffect();
        }
    }

    void tryEffect() {
        Utils.addToBotAbstract(() -> {
            AbstractPlayer p = AbstractDungeon.player;
            CardGroup hand = p.hand;
            AbstractCard c;
            boolean hasAttackCard = false;
            for (int i = 0; i < hand.size(); i++) {
                c = hand.group.get(i);
                if (c.type == AbstractCard.CardType.ATTACK) {
                    hasAttackCard = true;
                    break;
                }
            }
            if (!hasAttackCard) {
                flash();
                addToTop(new DrawCardByFilterAction(1, (x) -> x.type == AbstractCard.CardType.ATTACK));
            }
        });
    }
}