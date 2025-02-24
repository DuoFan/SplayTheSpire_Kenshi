package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.relic.YanXue;

public class PoBai extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(PoBai.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PoBai(AbstractCreature owner, int _amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;

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
        botToCheckEffectable();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        updateDescription();
        botToCheckEffectable();
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        super.wasHPLost(info, damageAmount);
        botToCheckEffectable();
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], getHealthThreshold());
    }

    int getHealthThreshold() {
        if (!(owner instanceof AbstractMonster)) {
            return 0;
        }

        float percentage = 25;

        if (amount > 1) {
            AbstractMonster m = (AbstractMonster) owner;

            int multple = 5;
            if (m.type == AbstractMonster.EnemyType.BOSS) {
                multple = 2;
            }

            percentage = 25 + (amount - 1) * multple;
        }

        int result = (int) (owner.maxHealth * percentage * 0.01);

        return Math.min(result, owner.maxHealth);
    }

    void botToCheckEffectable() {
        if (owner.isDead || owner.isDying || owner.halfDead) {
            return;
        }
        Utils.addToTopAbstract(() -> {
            if (owner.isDead || owner.isDying || owner.halfDead) {
                return;
            }
            if (owner.currentHealth <= getHealthThreshold()) {
                this.addToTop(new InstantKillAction(owner));
            }
        });
    }
}