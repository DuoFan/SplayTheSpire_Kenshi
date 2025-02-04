package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;

import java.security.InvalidParameterException;

public class Liu_StateMachine {
    static Liu_StateMachine instance;

    public static Liu_StateMachine getInstance(){
        if(instance == null){
            instance = new Liu_StateMachine();
        }
        return instance;
    }

    State state;

    boolean first_FZL;

    public void clearFlags(){
        first_FZL = false;
    }

    public void reset(){
        if(state != null){
            state.exit();
            state = null;
        }
    }

    public void changeLiu(StateEnum stateEnum){
        if(isStateMatch(stateEnum)){
            return;
        }

        changeStateTo(stateEnum);

        EventManager.getInstance().notifyEvent(EventKey.ON_LIU_CHANGED,
                this,null);

        if(!first_FZL && isStateMatch(StateEnum.FengZhiLiu)){
            first_FZL = true;
            EventManager.getInstance().notifyEvent(EventKey.FIRST_FZL_ON_TURN,
                    this,null);
        }
    }

    private void changeStateTo(StateEnum stateEnum){
        reset();
        switch (stateEnum){
            case FengZhiLiu:
                state = new FengZhiLiu_State();
                break;
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
        state.enter();
    }

    public boolean isStateMatch(StateEnum stateEnum){
        if(state == null){
            return false;
        }

        switch (stateEnum){
            case FengZhiLiu:
                return state.getPowerID().equals(FengZhiLiu.POWER_ID);
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
    }

    public enum StateEnum{
        FengZhiLiu
    }

    abstract class State{
        public abstract String getPowerID();
        public abstract void enter();

        public void exit() {
            if(AbstractDungeon.player.hasPower(getPowerID())){
                AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player,AbstractDungeon.player,getPowerID()));
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
}
