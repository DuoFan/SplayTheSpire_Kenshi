package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;
import game.duofan.common.Utils;

import java.security.InvalidParameterException;

public class Shi_StateMachine {

    static Shi_StateMachine instance;

    public static Shi_StateMachine getInstance() {
        if (instance == null) {
            instance = new Shi_StateMachine();
        }
        return instance;
    }

    State state;

    public Shi_StateMachine() {
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_BATTLE_START, new BattleStartListener());
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_TURN_START, new TurnStartListener());
    }

    public void reset() {
        if (state != null) {
            state.exit();
            state = null;
        }
    }

    public void update() {
        if (state != null) {
            state.update();
            if (isStateMatch(StateEnum.GongShi) && getGongShi_Accumulate() >= 3) {
                changeStateTo(StateEnum.ZhongShi, 1);
            } else if (state.amount == 0) {
                if (isStateMatch(StateEnum.JiaShi)) {
                    changeStateTo(StateEnum.GongShi, 3);
                } else if (isStateMatch(StateEnum.ZhongShi)) {
                    changeStateTo(StateEnum.JiaShi, 2);
                }
            }
        }
    }

    void changeStateTo(StateEnum stateEnum, int amount) {
        int oldStateAmount = getStateAmount();
        boolean isOldJiaShi = isStateMatch(StateEnum.JiaShi);
        boolean isOldGongShi = isStateMatch(StateEnum.GongShi);

        reset();
        switch (stateEnum) {
            case JiaShi:
                state = new JiaShi_State(amount);
                break;
            case GongShi:
                state = new GongShi_State(amount);
                break;
            case ZhongShi:
                state = new ZhongShi_State(amount);
                break;
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
        state.enter();

        boolean isNewGongShi = isStateMatch(StateEnum.GongShi);
        boolean isNewZhongShi = isStateMatch(StateEnum.ZhongShi);

        if (isOldJiaShi && isNewGongShi) {
            EventManager.getInstance().notifyEvent(EventKey.ON_JIASHI_TO_GONGSHI, this
                    , oldStateAmount);
        } else if (isOldGongShi && isNewZhongShi) {
            EventManager.getInstance().notifyEvent(EventKey.ON_GONGSHI_TO_ZHONGSHI, this
                    , oldStateAmount);
        }
    }

    public void addPower(StateEnum stateEnum, int amount) {
        if (!isStateMatch(stateEnum)) {
            changeStateTo(stateEnum, amount);
        } else {
            state.addAmount(amount);
        }
    }

    public int getGongShi_Accumulate() {
        if (!isStateMatch(StateEnum.GongShi)) {
            return 0;
        }

        GongShi_State gongShiState = (GongShi_State) state;

        return gongShiState.accmulate;
    }

    int getStateAmount() {
        if (state == null) {
            return 0;
        }
        return state.amount;
    }

    public boolean isStateMatch(StateEnum stateEnum) {
        if (state == null) {
            return false;
        }

        switch (stateEnum) {
            case JiaShi:
                return state.getPowerID().equals(JiaShi.POWER_ID);
            case GongShi:
                return state.getPowerID().equals(GongShi.POWER_ID);
            case ZhongShi:
                return state.getPowerID().equals(ZhongShi.POWER_ID);
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
    }

    public boolean isStateValid(StateEnum stateEnum) {

        if (!isStateMatch(stateEnum)) {
            return false;
        }

        return state != null && state.amount > 0;
    }

    public enum StateEnum {
        JiaShi, GongShi, ZhongShi
    }

    abstract class State {
        int amount;

        public abstract String getPowerID();

        public void addAmount(int _amount) {
            amount += _amount;
        }

        public void enter() {
            int temp = amount;
            amount = 0;
            addAmount(temp);
            log();
        }

        public void update() {
            if (amount > 0) {
                amount--;
                AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(AbstractDungeon.player, AbstractDungeon.player, getPowerID(), 1));
                log();
            }
        }

        public void exit() {
            if (AbstractDungeon.player.hasPower(getPowerID())) {
                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, getPowerID()));
            }
            amount = 0;
        }

        public void log() {
            Utils.addToBotAbstract(() -> {
                if (AbstractDungeon.player.hasPower(getPowerID())) {
                    System.out.println(getPowerID() + "___amount:" + amount + ",powerAmount" + AbstractDungeon.player.getPower(getPowerID()).amount);
                } else {
                    System.out.println(getPowerID() + "___amount:" + amount + ",未拥有该能力");
                }
            });
        }

        public State(int _amount) {
            amount = _amount;
        }
    }

    class JiaShi_State extends State {

        public JiaShi_State(int _amount) {
            super(_amount);
        }

        @Override
        public String getPowerID() {
            return JiaShi.POWER_ID;
        }

        @Override
        public void addAmount(int _amount) {
            super.addAmount(_amount);
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new JiaShi(AbstractDungeon.player, _amount)));
        }
    }

    class GongShi_State extends State {

        int accmulate;

        public GongShi_State(int _amount) {
            super(_amount);
        }

        @Override
        public String getPowerID() {
            return GongShi.POWER_ID;
        }

        @Override
        public void addAmount(int _amount) {
            super.addAmount(_amount);
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new GongShi(AbstractDungeon.player, _amount)));
        }

        @Override
        public void update() {
            if (amount > 0) {
                accmulate++;
            }
            super.update();
        }

        public int getAccmulate() {
            return accmulate;
        }
    }

    class ZhongShi_State extends State {

        public ZhongShi_State(int _amount) {
            super(_amount);
        }

        @Override
        public String getPowerID() {
            return ZhongShi.POWER_ID;
        }

        @Override
        public void addAmount(int _amount) {
            super.addAmount(_amount);
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ZhongShi(AbstractDungeon.player, _amount)));
        }
    }

    class BattleStartListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            reset();
        }
    }

    class TurnStartListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            addPower(Shi_StateMachine.StateEnum.JiaShi, 2);
        }
    }
}
