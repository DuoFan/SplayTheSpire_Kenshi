package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;
import game.duofan.common.Utils;

import java.security.InvalidParameterException;

public class Liu_StateMachine implements IEventListener {
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

    public Liu_StateMachine() {
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_BATTLE_START, this);
    }

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
                || CheckNotify_FirstLiu_OnTurn(StateEnum.ShanZhiLiu, EventKey.FIRST_SZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.DuanZhiLiu, EventKey.FIRST_DZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.YuZhiLiu, EventKey.FIRST_YuZL_ON_TURN)) {

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

    public StateEnum getLiu() {
        if (state == null) {
            return StateEnum.None;
        }

        if (isStateMatch(StateEnum.FengZhiLiu)) {
            return StateEnum.FengZhiLiu;
        } else if (isStateMatch(StateEnum.YingZhiLiu)) {
            return StateEnum.YingZhiLiu;
        } else if (isStateMatch(StateEnum.XiaZhiLiu)) {
            return StateEnum.XiaZhiLiu;
        } else if (isStateMatch(StateEnum.WeiZhiLiu)) {
            return StateEnum.WeiZhiLiu;
        } else if (isStateMatch(StateEnum.ShanZhiLiu)) {
            return StateEnum.ShanZhiLiu;
        } else if (isStateMatch(StateEnum.DuanZhiLiu)) {
            return StateEnum.DuanZhiLiu;
        } else if (isStateMatch(StateEnum.YuZhiLiu)) {
            return StateEnum.YuZhiLiu;
        }

        return StateEnum.None;
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
            case DuanZhiLiu:
                state = new DuanZhiLiu_State();
                break;
            case YuZhiLiu:
                state = new YuZhiLiu_State();
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
            case DuanZhiLiu:
                return powerID.equals(DuanZhiLiu.POWER_ID);
            case YuZhiLiu:
                return powerID.equals(YuZhiLiu.POWER_ID);
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
    }

    @Override
    public void OnEvent(Object sender, Object e) {
        Liu_StateMachine.getInstance().clearFlags();
        Liu_StateMachine.getInstance().clearLastEffectLiuCardOnTurn();
        Liu_StateMachine.getInstance().clearLastEffectLiuCardOnBattle();
        Liu_StateMachine.getInstance().reset();
    }

    public enum StateEnum {
        None(0), FengZhiLiu(1), YingZhiLiu(2), XiaZhiLiu(4),
        WeiZhiLiu(8), ShanZhiLiu(16), DuanZhiLiu(32),
        YuZhiLiu(64), YanZhiLiu(128), All(-1);

        private int value = 0;

        private StateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
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

    class DuanZhiLiu_State extends State {
        @Override
        public String getPowerID() {
            return DuanZhiLiu.POWER_ID;
        }

        @Override
        public void enter() {
            Utils.playerGainPower(new DuanZhiLiu(AbstractDungeon.player));
        }
    }

    class YuZhiLiu_State extends State {
        @Override
        public String getPowerID() {
            return YuZhiLiu.POWER_ID;
        }

        @Override
        public void enter() {
            Utils.playerGainPower(new YuZhiLiu(AbstractDungeon.player));
        }
    }
}
