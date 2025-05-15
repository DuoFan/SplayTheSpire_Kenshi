package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.*;

import java.util.ArrayList;

public class YuGan extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(YuGan.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    int loseAmount;

    public YuGan(AbstractCreature owner, int amount) {
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

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], amount, loseAmount);
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        loseAmount = 1;
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        loseAmount++;
        updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        effect();
    }

    public void effect() {
        ArrayList<AbstractMonster> monsters = Utils.getAllAliveMonsters();

        for (int i = 0; i < monsters.size(); i++) {
            AbstractMonster m = monsters.get(i);
            if (Utils.isIntentAttack(m)) {
                Utils.playerGainBlock(amount);
                /*addToBot(new DrawCardAction(1));
                this.addToBot(new LoseEnergyAction(loseAmount));*/
                flash();
                break;
            }
        }
    }
}