package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
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

    ShuangJianHeBi_Card selfCard;

    public ShuangJianHeBi(AbstractCreature owner, ShuangJianHeBi_Card t) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        selfCard = t;
        this.type = PowerType.BUFF;
        this.owner = owner;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0].replace("[NAME]", selfCard.name);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card.type == AbstractCard.CardType.ATTACK) {
            AbstractMonster m = null;
            if (action.target != null && action.target instanceof AbstractMonster) {
                m = (AbstractMonster) action.target;
            }
            ShuangJianHeBi_Card tmp = (ShuangJianHeBi_Card) selfCard.makeStatEquivalentCopy();

            AbstractDungeon.player.limbo.addToBottom(tmp);
            tmp.current_x = card.current_x;
            tmp.current_y = card.current_y;
            tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            tmp.target_y = (float) Settings.HEIGHT / 2.0F;
            if(m != null){
                tmp.calculateCardDamage(m);
            }

            tmp.duplicate = true;
            tmp.purgeOnUse = true;
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
            Utils.removePowerTop(AbstractDungeon.player, ID);
        }
    }
}
