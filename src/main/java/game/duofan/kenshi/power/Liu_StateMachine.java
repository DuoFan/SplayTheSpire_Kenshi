package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;

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

    public void clearFlags() {
        firstFlag = 0;
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

        if (CheckNotify_FirstLiu_OnTurn(StateEnum.FengZhiLiu,EventKey.FIRST_FZL_ON_TURN)
        || CheckNotify_FirstLiu_OnTurn(StateEnum.YingZhiLiu,EventKey.FIRST_YZL_ON_TURN)) {

        }
    }

    boolean CheckNotify_FirstLiu_OnTurn(StateEnum stateEnum,String eventKey){
        int flag = firstFlag & stateEnum.value;
        if(flag == 1){
            System.out.println("-------已进入" + stateEnum);
            return false;
        }

        if(!isStateMatch(stateEnum)){
            System.out.println("-------未进入" + stateEnum);
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
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
    }

    public enum StateEnum {
        FengZhiLiu(1), YingZhiLiu(2);

        private int value = 0;
        private  StateEnum(int value){
            this.value = value;
        }

        public int getValue(){
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
}
