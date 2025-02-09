package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.Utils;

import java.security.InvalidParameterException;

public class Liu_StateMachine {
    static Liu_StateMachine instance;

    public static Liu_StateMachine getInstance() {
        if (instance == null) {
            instance = new Liu_StateMachine();
        }
        return instance;
    }

    State state;

    int firstFlag;

    AbstractCard lastEffectLiuCardOnTurn;
    AbstractCard lastEffectLiuCardOnBattle;

    public void clearFlags() {
        firstFlag = 0;
    }

    public void clearLastEffectLiuCardOnTurn() {
        lastEffectLiuCardOnTurn = null;
    }

    public void clearLastEffectLiuCardOnBattle() {
        lastEffectLiuCardOnBattle = null;
    }

    public void setLastEffectLiuCardOnTurn(AbstractCard card) {
        lastEffectLiuCardOnTurn = card;
    }

    public AbstractCard getLastEffectLiuCardOnTurn() {
        return lastEffectLiuCardOnTurn;
    }

    public void setLastEffectLiuCardOnBattle(AbstractCard lastEffectLiuCardOnBattle) {
        this.lastEffectLiuCardOnBattle = lastEffectLiuCardOnBattle;
    }

    public AbstractCard getLastEffectLiuCardOnBattle() {
        return lastEffectLiuCardOnBattle;
    }

    public void reset() {
        if (state != null) {
            state.exit();
            state = null;
        }
    }

    public void changeLiu(StateEnum stateEnum) {
        if (isStateMatch(stateEnum)) {
            return;
        }

        changeStateTo(stateEnum);

        EventManager.getInstance().notifyEvent(EventKey.ON_LIU_CHANGED,
                this, null);

        if (CheckNotify_FirstLiu_OnTurn(StateEnum.FengZhiLiu, EventKey.FIRST_FZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.YingZhiLiu, EventKey.FIRST_YZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.XiaZhiLiu, EventKey.FIRST_XZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.WeiZhiLiu, EventKey.FIRST_WZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.ShanZhiLiu, EventKey.FIRST_SZL_ON_TURN)) {

        }
    }

    public boolean hasLiuFlag(StateEnum stateEnum, StateEnum liu) {
        System.out.println("----------------liu:" + liu + "result:" + hasLiuFlag(stateEnum.value, liu));
        return hasLiuFlag(stateEnum.value, liu);
    }

    public boolean hasLiuFlag(int flag, StateEnum liu) {
        return (flag & liu.value) == liu.value;
    }

    boolean CheckNotify_FirstLiu_OnTurn(StateEnum stateEnum, String eventKey) {
        if (!isStateMatch(stateEnum)) {
            System.out.println("-------未进入" + stateEnum);
            return false;
        }

        if (hasLiuFlag(firstFlag, stateEnum)) {
            System.out.println("-------已进入" + stateEnum);
            return false;
        }

        System.out.println("-------本回合首次进入" + stateEnum);

        firstFlag |= stateEnum.value;

        EventManager.getInstance().notifyEvent(eventKey,
                this, null);

        return true;
    }

    private void changeStateTo(StateEnum stateEnum) {
        reset();
        switch (stateEnum) {
            case FengZhiLiu:
                state = new FengZhiLiu_State();
                break;
            case YingZhiLiu:
                state = new YingZhiLiu_State();
                break;
            case XiaZhiLiu:
                state = new XiaZhiLiu_State();
                break;
            case WeiZhiLiu:
                state = new WeiZhiLiu_State();
                break;
            case ShanZhiLiu:
                state = new ShanZhiLiu_State();
                break;
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
        state.enter();
    }

    public boolean isStateMatch(StateEnum stateEnum) {
        if (state == null) {
            return false;
        }

        String powerID = state.getPowerID();

        switch (stateEnum) {
            case FengZhiLiu:
                return powerID.equals(FengZhiLiu.POWER_ID);
            case YingZhiLiu:
                return powerID.equals(YingZhiLiu.POWER_ID);
            case XiaZhiLiu:
                return powerID.equals(XiaZhiLiu.POWER_ID);
            case WeiZhiLiu:
                return powerID.equals(WeiZhiLiu.POWER_ID);
            case ShanZhiLiu:
                return powerID.equals(ShanZhiLiu.POWER_ID);
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
    }

    public enum StateEnum {
        FengZhiLiu(1), YingZhiLiu(2), XiaZhiLiu(4),
        WeiZhiLiu(8), ShanZhiLiu(16), All(-1);

        private int value = 0;

        private StateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void xorBit(StateEnum e) {
            value = value ^ e.value;
        }
    }

    abstract class State {
        public abstract String getPowerID();

        public abstract void enter();

        public void exit() {
            if (AbstractDungeon.player.hasPower(getPowerID())) {
                AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, getPowerID()));
            }
        }
    }

    class FengZhiLiu_State extends State {
        @Override
        public String getPowerID() {
            return FengZhiLiu.POWER_ID;
        }

        @Override
        public void enter() {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FengZhiLiu(AbstractDungeon.player)));
        }
    }

    class YingZhiLiu_State extends State {
        @Override
        public String getPowerID() {
            return YingZhiLiu.POWER_ID;
        }

        @Override
        public void enter() {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new YingZhiLiu(AbstractDungeon.player)));
        }
    }

    class XiaZhiLiu_State extends State {
        @Override
        public String getPowerID() {
            return XiaZhiLiu.POWER_ID;
        }

        @Override
        public void enter() {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new XiaZhiLiu(AbstractDungeon.player)));
        }
    }

    class WeiZhiLiu_State extends State {
        @Override
        public String getPowerID() {
            return WeiZhiLiu.POWER_ID;
        }

        @Override
        public void enter() {
            Utils.playerGainPower(new WeiZhiLiu(AbstractDungeon.player));
        }
    }

    class ShanZhiLiu_State extends State {
        @Override
        public String getPowerID() {
            return ShanZhiLiu.POWER_ID;
        }

        @Override
        public void enter() {
            Utils.playerGainPower(new ShanZhiLiu(AbstractDungeon.player));
        }
    }
}
