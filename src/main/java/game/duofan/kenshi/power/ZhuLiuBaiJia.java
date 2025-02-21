package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.*;
import game.duofan.kenshi.card.IQiMin;

import java.io.BufferedOutputStream;

public class ZhuLiuBaiJia extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(ZhuLiuBaiJia.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    XinSuiYiDong xinSuiYiDong;
    XinNianTongShen xinNianTongShen;

    static ZhuLiuBaiJia instance;

    public static boolean canForceInvokeLiu() {
        if (AbstractDungeon.player == null) {
            return false;
        }
        if (instance == null) {
            return false;
        }
        if (instance.xinNianTongShen == null && instance.xinSuiYiDong == null) {
            return false;
        }
        if (instance.xinSuiYiDong != null && instance.xinSuiYiDong.getTurnAmount() > 0) {
            return true;
        }
        return instance.xinNianTongShen != null;
    }

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
    public void onInitialApplication() {
        super.onInitialApplication();
        EventManager.getInstance().registerToEvent(EventKey.ON_GAIN_XIN_SUI_YI_DONG,
                new XinSuiYiDongListener());
        EventManager.getInstance().registerToEvent(EventKey.ON_GAIN_XIN_NIAN_TONG_SHEN,
                new XinNianTongShenListener());
        instance = this;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        Liu_StateMachine.StateEnum liu = Utils.getLiuFromCard(card);

        boolean isEffectAble = true;

        if (liu != Liu_StateMachine.StateEnum.None) {

            boolean isLiuMatch = Liu_StateMachine.getInstance().isStateMatch(liu);

            if (isLiuMatch || (xinSuiYiDong != null && xinSuiYiDong.getTurnAmount() > 0) || xinNianTongShen != null) {

                if (card instanceof IShanZhiLiuCard) {
                    IShanZhiLiuCard c = (IShanZhiLiuCard) card;
                    isEffectAble = c.effectable();
                }

                if (!isEffectAble) {
                    return;
                }

                if (card instanceof IXiaZhiLiuCard) {
                    Utils.invokeXZL_Effect((IXiaZhiLiuCard) card, false);
                } else {
                    Utils.invokeLiuCardEffectOnBottom(card);
                }
                Liu_StateMachine.instance.setLastEffectLiuCardOnTurn(card);
                Liu_StateMachine.instance.setLastEffectLiuCardOnBattle(card);


                if (xinSuiYiDong != null && xinSuiYiDong.getTurnAmount() > 0) {
                    xinSuiYiDong.subTurnAmountToEffect();
                }
                if (!isLiuMatch) {
                    Liu_StateMachine.getInstance().changeLiu(liu);
                    if (xinNianTongShen != null) {
                        xinNianTongShen.flash();
                    }
                }

                if (card instanceof IShanZhiLiuCard) {
                    IShanZhiLiuCard c = (IShanZhiLiuCard) card;
                    isEffectAble = c.effectable();
                }

                if (isEffectAble && Utils.getQiAmount() > 0) {
                    if (card instanceof IXiaZhiLiuCard) {
                        Utils.invokeXZL_Effect((IXiaZhiLiuCard) card, true);
                    } else {
                        Utils.invokeLiuCardEffectOnBottom(card);
                    }
                    if (card instanceof IQiMin) {
                        Utils.invokeLiuCardEffectOnBottom(card);
                    }
                    Utils.playerReduceQi(1);
                }

                if (isEffectAble && AbstractDungeon.player.hasPower(JiYiXingTai.POWER_ID)) {
                    Utils.invokeLiuCardEffectOnBottom(card);
                }
            } else {

                if (card instanceof IShanZhiLiuCard) {
                    IShanZhiLiuCard c = (IShanZhiLiuCard) card;
                    isEffectAble = c.effectable();
                }

                if (!isEffectAble) {
                    return;
                }

                Liu_StateMachine.getInstance().changeLiu(liu);
            }

            if (liu == Liu_StateMachine.StateEnum.YuZhiLiu) {
                EventManager.getInstance().notifyEvent(EventKey.ON_YU_CARD_PLAY,
                        this, card);
            }
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);
        LinkCardManager.getInstance().tryPlaySelfCard(card);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        super.onCardDraw(card);
        LinkCardManager.getInstance().tryDrawLinkedCard(card);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        Liu_StateMachine.getInstance().reset();
        Liu_StateMachine.getInstance().clearFlags();
        Liu_StateMachine.getInstance().clearLastEffectLiuCardOnTurn();
    }

    class XinSuiYiDongListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            xinSuiYiDong = (XinSuiYiDong) sender;
        }
    }

    class XinNianTongShenListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            xinNianTongShen = (XinNianTongShen) sender;
        }
    }
}
