package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import game.duofan.common.*;
import game.duofan.kenshi.card.FZL_HuiFengZhan;
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

    XinSuiYiDong xinSuiYiDong;

    static ZhuLiuBaiJia instance;

    public static boolean canForceInvokeLiu() {
        if (AbstractDungeon.player == null) {
            return false;
        }
        if (instance == null) {
            return false;
        }
        if (instance.xinSuiYiDong == null) {
            return false;
        }
        if (instance.xinSuiYiDong != null && instance.xinSuiYiDong.getTurnAmount() > 0) {
            return true;
        }
        return false;
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
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        EventManager.getInstance().registerToEvent(EventKey.ON_GAIN_XIN_SUI_YI_DONG,
                new XinSuiYiDongListener());
        instance = this;
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        super.onPlayCard(card, m);

        Liu_StateMachine.StateEnum curLiu = Liu_StateMachine.getInstance().getLiu();

        if (curLiu == Liu_StateMachine.StateEnum.None) {
            return;
        }

        Liu_StateMachine.StateEnum liu = Utils.getLiuFromCard(card);

        if (liu != Liu_StateMachine.StateEnum.None && liu != curLiu) {

            switch (curLiu) {
                case FengZhiLiu:
                    Utils.playerGainPower(new StrengthPower(AbstractDungeon.player, 1));
                    Utils.playerGainPower(new LoseStrengthPower(AbstractDungeon.player, 1));
                    break;
                case XiaZhiLiu:
                    Utils.playerGainQi(1);
                    break;
                case YuZhiLiu:
                    Utils.playerGainBlock(3);
                    break;
                case YanZhiLiu:
                    Utils.giveAllMonsterBaoYanDamage(1);
                    break;
            }
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        Liu_StateMachine.StateEnum liu = Utils.getLiuFromCard(card);

        if (liu != Liu_StateMachine.StateEnum.None) {

            boolean isLiuMatch = Liu_StateMachine.getInstance().isStateMatch(liu);

            if (isLiuMatch || (xinSuiYiDong != null && xinSuiYiDong.getTurnAmount() > 0)) {
                if (card instanceof IXiaZhiLiuCard) {
                    Utils.invokeXZL_Effect((IXiaZhiLiuCard) card, false);
                } else {
                    Utils.invokeLiuCardEffectWithTiming(card);
                }
                Liu_StateMachine.getInstance().setLastEffectLiuCardOnTurn(card);
                Liu_StateMachine.getInstance().setLastEffectLiuCardOnBattle(card);


                if (xinSuiYiDong != null && xinSuiYiDong.getTurnAmount() > 0) {
                    xinSuiYiDong.subTurnAmountToEffect();
                }
                if (!isLiuMatch) {
                    Liu_StateMachine.getInstance().changeLiu(liu);
                }

                if (Utils.getQiAmount() > 0) {
                    AbstractPower qi = AbstractDungeon.player.getPower(Qi.POWER_ID);
                    if (qi != null) {
                        qi.flash();
                    }
                    if (card instanceof IXiaZhiLiuCard) {
                        Utils.invokeXZL_Effect((IXiaZhiLiuCard) card, true);
                    } else {
                        Utils.invokeLiuCardEffectWithTiming(card);
                    }
                    if (card instanceof IQiMin) {
                        Utils.invokeLiuCardEffectWithTiming(card);
                    }
                    Utils.playerReduceQi(1);
                }
            } else {
                Liu_StateMachine.getInstance().changeLiu(liu);
            }

            if (liu == Liu_StateMachine.StateEnum.YuZhiLiu) {
                EventManager.getInstance().notifyEvent(EventKey.ON_YU_CARD_PLAY,
                        this, card);
            }
        }
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
}
