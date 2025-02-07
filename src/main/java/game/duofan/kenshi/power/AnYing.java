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

public class AnYing extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(AnYing.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    static float extraBuff;

    public AnYing(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public static void setExtraBuff(float v) {
        extraBuff = v;
    }

    public void updateDescription() {
        int value = (int) ((calculateBuff() - 1) * 100);
        this.description = String.format(DESCRIPTIONS[0], value);
    }

    float calculateBuff() {
        return 1.5f + extraBuff;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? damage * calculateBuff() : damage;
    }

    @Override
    public float modifyBlock(float blockAmount) {
        return 0;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card.type == AbstractCard.CardType.ATTACK) {
            AbstractPlayer p = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(p, p, POWER_ID));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(p, p, POWER_ID));
    }
}