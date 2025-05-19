package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;
import game.duofan.common.*;

public class XiuLuo extends AbstractPower {

    public static final String POWER_ID = IDManager.getInstance().getID(XiuLuo.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    int initAmount;
    int effectAmount = 0;

    public XiuLuo(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = amount;
        initAmount = amount;

        String path128 = "img/powers/xiuLuoPower84.png";
        String path48 = "img/powers/xiuLuoPower32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        amount = initAmount;
        effectAmount = 0;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);
        if (card.type == AbstractCard.CardType.ATTACK) {
            amount--;
            if (amount > 0) {
                updateDescription();
            } else {
                effect();
            }
        }
    }

    void effect() {
        effectAmount++;
        amount = initAmount * (int) Math.pow(2, effectAmount);
        flash();
        updateDescription();

        this.addToBot(new VFXAction(new WhirlwindEffect(new Color(1.0F, 0.9F, 0.4F, 1.0F), true)));
        this.addToBot(new SkipEnemiesTurnAction());
        this.addToBot(new PressEndTurnButtonAction());
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        this.updateDescription();
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }
}
