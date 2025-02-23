package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.TaYinAction;
import game.duofan.kenshi.card.ManMa;
import game.duofan.kenshi.card.WZL_TaYin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ZaiLaiYiZhang extends AbstractPower {

    static int idIndex;

    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(ZaiLaiYiZhang.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    WZL_TaYin self;

    AbstractCard targetCard;

    public ZaiLaiYiZhang(WZL_TaYin _self, AbstractCard t) {
        self = _self;
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        owner = AbstractDungeon.player;
        targetCard = t;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0].replace("[NAME]", targetCard.name);
    }

    @Override
    public void onExhaust(AbstractCard card) {
        super.onExhaust(card);
        if (card == targetCard) {
            addToBot(new TaYinAction(null, self.upgraded));
            Utils.removePower(owner, this.ID);
        }
    }

    @Override
    public void atEndOfRound() {
        super.atEndOfRound();
        Utils.removePower(owner, this.ID);
    }
}
