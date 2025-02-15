package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.sun.org.apache.bcel.internal.generic.FADD;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.IQiMin;

public class ZhuLiuBaiJia extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(ZhuLiuBaiJia.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ZhuLiuBaiJia(AbstractCreature owner) {
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

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount, this.amount, Shi_StateMachine.getInstance().getGongShi_Accumulate());
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        Liu_StateMachine.StateEnum liu = Utils.getLiuFromCard(card);

        if (liu != Liu_StateMachine.StateEnum.None) {
            if (Liu_StateMachine.getInstance().isStateMatch(liu)) {
                boolean isEffectAble = true;

                if (card instanceof IShanZhiLiuCard) {
                    IShanZhiLiuCard c = (IShanZhiLiuCard) card;
                    isEffectAble = c.effectable();
                }

                if (!isEffectAble) {
                    return;
                }

                if (card instanceof IXiaZhiLiuCard) {
                    Utils.invokeXZL_Effect((IXiaZhiLiuCard) card, false);
                }
                else{
                    Utils.invokeLiuCardEffect(card);
                }
                Liu_StateMachine.instance.setLastEffectLiuCardOnTurn(card);
                Liu_StateMachine.instance.setLastEffectLiuCardOnBattle(card);


                if (card instanceof IShanZhiLiuCard) {
                    IShanZhiLiuCard c = (IShanZhiLiuCard) card;
                    isEffectAble = c.effectable();
                }

                if (isEffectAble && Utils.getQiAmount() > 0) {
                    if (card instanceof IXiaZhiLiuCard) {
                        Utils.invokeXZL_Effect((IXiaZhiLiuCard) card, true);
                    }
                    else{
                        Utils.invokeLiuCardEffect(card);
                    }
                    if (card instanceof IQiMin) {
                        Utils.invokeLiuCardEffect(card);
                    }
                    Utils.playerReduceQi(1);
                }

                if (isEffectAble && AbstractDungeon.player.hasPower(JiYiXingTai.POWER_ID)) {
                    Utils.invokeLiuCardEffect(card);
                }
            } else {
                Liu_StateMachine.getInstance().changeLiu(liu);
            }
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);
        LinkCardManager.instance.tryPlaySelfCard(card);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        super.onCardDraw(card);
        LinkCardManager.instance.tryDrawLinkedCard(card);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        Liu_StateMachine.getInstance().reset();
        Liu_StateMachine.getInstance().clearFlags();
        Liu_StateMachine.getInstance().clearLastEffectLiuCardOnTurn();
    }
}
