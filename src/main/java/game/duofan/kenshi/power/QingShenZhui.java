package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;

public class QingShenZhui extends AbstractPower {

    static int idIndex;

    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(QingShenZhui.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    int drawAmount;
    int damageAmount;
    AbstractMonster source;
    boolean isNextTurn;

    public QingShenZhui(AbstractCreature owner,int _drawAmount) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        this.owner = owner;
        this.type = PowerType.BUFF;

        damageAmount = 0;
        drawAmount = _drawAmount;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);
        this.updateDescription();
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int _damageAmount) {
        damageAmount += _damageAmount;
        if(info.owner instanceof AbstractMonster){
            source = (AbstractMonster)info.owner;
        }
        updateDescription();
        return 0;
    }

    public void updateDescription() {
        String description = String.format(DESCRIPTIONS[0], damageAmount,drawAmount);
        this.description = description;
    }

    @Override
    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        isNextTurn = true;
        if(drawAmount > 0){
            Utils.playerDrawCardByClass(drawAmount,IYuZhiLiuCard.class);
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);
        if(!(card instanceof IYuZhiLiuCard) && isNextTurn){
            dispose();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if(isPlayer && isNextTurn){
            dispose();
        }
    }

    void dispose(){
        AbstractPlayer p = AbstractDungeon.player;
        if(damageAmount > 0)
        {
            addToTop(new DamageAction(p,new DamageInfo(source,damageAmount, DamageInfo.DamageType.NORMAL)));
        }
        addToTop(new RemoveSpecificPowerAction(p,p,ID));
    }
}