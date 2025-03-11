package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.*;
import game.duofan.kenshi.relic.YiQi;

public class ZhanYanLuo extends AbstractPower {

    static final String POWER_ID = IDManager.getInstance().getID(ZhanYanLuo.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ZhanYanLuo(AbstractMonster m) {
        this.name = NAME;
        this.ID = POWER_ID;
        owner = m;
        this.type = PowerType.DEBUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public void updateDescription() {
        if (owner == null) {
            this.description = "斩阎罗";
        } else {
            this.description = DESCRIPTIONS[0].replace("[NAME]", owner.name);
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);

        Utils.addToTopAbstract(() -> {
            if (!owner.hasPower(ID)) {
                return;
            }
            if ((owner.isDying || owner.currentHealth <= 0) && !owner.halfDead) {
                if (AbstractDungeon.player.hasRelic(YiQi.ID)) {
                    return;
                }
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                        Settings.WIDTH / 2.0f,
                        Settings.HEIGHT / 2.0f,
                        new YiQi() // 你的自定义遗物实例
                );
                Utils.removePower(owner, ID);
                EventManager.getInstance().notifyEvent(EventKey.ON_GAIN_YI_QI, this, null);
            }
        });
    }
}
