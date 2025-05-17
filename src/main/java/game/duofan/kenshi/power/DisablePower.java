package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.*;

public class DisablePower extends AbstractPower {
    static final String ORIGINAL_ID = IDManager.getInstance().getID(DisablePower.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGINAL_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    AbstractPower originPower;

    public AbstractPower getOriginPower() {
        return originPower;
    }

    public DisablePower(AbstractPower _originPower) {
        originPower = _originPower;
        this.name = originPower.name;
        this.ID = _originPower.ID + "_disabled";
        this.owner = _originPower.owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = originPower.amount;

        originPower = _originPower;

        this.region128 = originPower.region128;
        this.region48 = originPower.region48;

        onInitialApplication();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        float sinValue = (float) Math.sin(System.currentTimeMillis() * 0.002);
        Color pulseGray = new Color(0.5f, 0.5f, 0.5f, 0.5f + sinValue * 0.3f);

        super.renderIcons(sb, x, y, pulseGray);
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        int index = owner.powers.indexOf(this);
        if (index >= 0 && originPower != null) {
            originPower.stackPower(amount - originPower.amount);
            owner.powers.set(index, originPower);
            AbstractDungeon.onModifyPower();
        }
        dispose();
    }

    @Override
    public void onVictory() {
        super.onVictory();
        dispose();
    }

    @Override
    public void onRemove() {
        super.onRemove();
        dispose();
    }

    @Override
    public void onDeath() {
        super.onDeath();
        dispose();
    }

    void dispose() {
        DisablePowerManager.getInstance().removeDisablePower(this);
    }
}