package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.*;
import game.duofan.kenshi.card.ShuangJianHeBi_Card;

public class ShuangJianHeBi extends AbstractPower {

    static int idIndex;

    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(ShuangJianHeBi.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    AbstractCard c1;
    AbstractCard c2;

    boolean c1Played;
    boolean c2Played;

    public ShuangJianHeBi(AbstractCreature owner, AbstractCard _c1, AbstractCard _c2) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        this.type = PowerType.BUFF;
        c1 = _c1;
        c2 = _c2;
        this.owner = owner;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "img/powers/ShuangJianHeBiPower84.png";
        String path48 = "img/powers/ShuangJianHeBiPower32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0].replace("[NAME1]", c1.name).replace("[NAME2]", c2.name);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card.equals(c1)) {
            c1Played = true;
        } else if (card.equals(c2)) {
            c2Played = true;
        }

        if (c1Played && c2Played) {
            Utils.playerGainEnergy(1);
            Utils.removePowerTop(AbstractDungeon.player, ID);
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        Utils.removePowerTop(AbstractDungeon.player, ID);
    }
}
