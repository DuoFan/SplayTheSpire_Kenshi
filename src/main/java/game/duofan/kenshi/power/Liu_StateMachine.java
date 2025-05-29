package game.duofan.kenshi.power;

import basemod.BaseMod;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PostDeathSubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IEventListener;
import game.duofan.common.Utils;
import game.duofan.kenshi.liuMachineRenderer.LiuMachineRenderer;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;

public class Liu_StateMachine implements IEventListener, PostBattleSubscriber, PostDeathSubscriber, PostUpdateSubscriber, PostDungeonInitializeSubscriber {
    private static Liu_StateMachine instance;

    public static Liu_StateMachine getInstance() {
        if (instance == null) {
            instance = new Liu_StateMachine();
        }
        return instance;
    }

    public static boolean existInstance() {
        return instance != null;
    }

    State state;

    int firstFlag;
    int enterLiuAmountInTurn;
    boolean needRender;

    AbstractCard lastEffectLiuCardOnTurn;
    AbstractCard lastEffectLiuCardOnBattle;

    HashMap<StateEnum, StateEnum> drivingMap;

    LiuMachineRenderer machineRenderer;

    ArrayList<StateEnum> drivers;
    ArrayList<StateEnum> invokables;

    StateEnum lastLiu;

    public Liu_StateMachine() {
        EventManager.getInstance().registerToPersistEvent(EventKey.ON_BATTLE_START, this);
        BaseMod.subscribe(this);
        drivingMap = new HashMap<>();
        drivers = new ArrayList<StateEnum>();
        invokables = new ArrayList<StateEnum>();
        machineRenderer = new LiuMachineRenderer();
        machineRenderer.init();
    }

    public void clearDrivingMap() {
        drivingMap.clear();
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

    public void clearEnterLiuAmountOnTurn() {
        enterLiuAmountInTurn = 0;
    }

    public int getEnterLiuAmountInTurn(){
        return enterLiuAmountInTurn;
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
            EventManager.getInstance().notifyEvent(EventKey.ON_LIU_EXITED, this, null);
        }
    }

    public void render(SpriteBatch sb) {
        machineRenderer.render(sb);
    }

    public void changeLiu(StateEnum stateEnum) {
        if (isStateMatch(stateEnum)) {
            return;
        }

        enterLiuAmountInTurn++;

        needRender = true;

        changeStateTo(stateEnum);

        EventManager.getInstance().notifyEvent(EventKey.ON_LIU_CHANGED,
                this, stateEnum);

        if (CheckNotify_FirstLiu_OnTurn(StateEnum.FengZhiLiu, EventKey.FIRST_FZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.XiaZhiLiu, EventKey.FIRST_XZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.YuZhiLiu, EventKey.FIRST_YuZL_ON_TURN)
                || CheckNotify_FirstLiu_OnTurn(StateEnum.YanZhiLiu, EventKey.FIRST_YanZL_ON_TURN)) {
        }
    }

    public boolean hasLiuFlag(StateEnum stateEnum, StateEnum liu) {
        return hasLiuFlag(stateEnum.value, liu);
    }

    public boolean hasLiuFlag(int flag, StateEnum liu) {
        return (flag & liu.value) == liu.value;
    }

    boolean CheckNotify_FirstLiu_OnTurn(StateEnum stateEnum, String eventKey) {
        if (!isStateMatch(stateEnum)) {
            return false;
        }

        if (hasLiuFlag(firstFlag, stateEnum)) {
            return false;
        }

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
        } else if (isStateMatch(StateEnum.XiaZhiLiu)) {
            return StateEnum.XiaZhiLiu;
        } else if (isStateMatch(StateEnum.YuZhiLiu)) {
            return StateEnum.YuZhiLiu;
        } else if (isStateMatch(StateEnum.YanZhiLiu)) {
            return StateEnum.YanZhiLiu;
        }

        return StateEnum.None;
    }

    public StateEnum lastLiu() {
        return lastLiu;
    }

    private void changeStateTo(StateEnum stateEnum) {
        lastLiu = getLiu();
        reset();
        switch (stateEnum) {
            case FengZhiLiu:
                state = new FengZhiLiu_State();
                break;
            case XiaZhiLiu:
                state = new XiaZhiLiu_State();
                break;
            case YuZhiLiu:
                state = new YuZhiLiu_State();
                break;
            case YanZhiLiu:
                state = new YanZhiLiu_State();
                break;
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
        state.enter();

        Liu_Tutorial tutorial = new Liu_Tutorial();
        tutorial.tryShowTutorial1();
    }

    public boolean isStateMatch(StateEnum stateEnum) {
        if (state == null) {
            return false;
        }

        String powerID = state.getPowerID();

        switch (stateEnum) {
            case FengZhiLiu:
                return powerID.equals(FengZhiLiu.POWER_ID);
            case XiaZhiLiu:
                return powerID.equals(XiaZhiLiu.POWER_ID);
            case YuZhiLiu:
                return powerID.equals(YuZhiLiu.POWER_ID);
            case YanZhiLiu:
                return powerID.equals(YanZhiLiu.POWER_ID);
            default:
                throw new InvalidParameterException("无法找到匹配项" + stateEnum);
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        System.out.println("-------------------战斗结束时处理流派状态");
        clearAll();
    }

    @Override
    public void receivePostDeath() {
        System.out.println("-------------------死亡时处理流派状态");
        clearAll();
    }

    @Override
    public void receivePostUpdate() {
        if (!CardCrawlGame.isInARun() && getLiu() != StateEnum.None) {
            System.out.println("-------------------退出游戏时处理流派状态");
            clearAll();
        }
    }

    @Override
    public void receivePostDungeonInitialize() {
        System.out.println("-------------------地牢初始化时处理流派状态");
        clearAll();
    }

    void clearAll() {
        clearDrivingMap();
        clearFlags();
        clearLastEffectLiuCardOnTurn();
        clearLastEffectLiuCardOnBattle();
        clearEnterLiuAmountOnTurn();
        state = null;
        needRender = false;
        lastLiu = StateEnum.None;
    }

    @Override
    public void OnEvent(Object sender, Object e) {
        clearDrivingMap();
        clearFlags();
        clearLastEffectLiuCardOnTurn();
        clearLastEffectLiuCardOnBattle();
        clearEnterLiuAmountOnTurn();
        reset();
        needRender = false;
        lastLiu = StateEnum.None;
    }

    public ArrayList<StateEnum> getInvokeable(StateEnum liu) {
        invokables.clear();

        AbstractPower _xinSuiYiDong = AbstractDungeon.player.getPower(XinSuiYiDong.POWER_ID);
        XinSuiYiDong xinSuiYiDong = null;
        if (_xinSuiYiDong != null) {
            xinSuiYiDong = (XinSuiYiDong) _xinSuiYiDong;
        }

        if (liu == StateEnum.None && (xinSuiYiDong == null || xinSuiYiDong.getTurnAmount() <= 0)) {
            return invokables;
        }

        invokables.add(StateEnum.FengZhiLiu);
        invokables.add(StateEnum.XiaZhiLiu);
        invokables.add(StateEnum.YanZhiLiu);
        invokables.add(StateEnum.YuZhiLiu);

        if (xinSuiYiDong != null && xinSuiYiDong.getTurnAmount() > 0) {
            return invokables;
        } else {
            //result.remove(liu);
        }

        getDrivers(liu);
        invokables.removeAll(drivers);

        if (liu.equals(StateEnum.FengZhiLiu)) {
            AbstractPower baiHuaQiFang = AbstractDungeon.player.getPower(BaiHuaQiFang.POWER_ID);
            if (baiHuaQiFang != null && invokables.indexOf(StateEnum.FengZhiLiu) < 0) {
                invokables.add(StateEnum.FengZhiLiu);
            }
        }

        return invokables;
    }

    public void setDriving(StateEnum from, StateEnum target) {
        if (from == StateEnum.None) {
            return;
        }
        drivingMap.put(from, target);
        Liu_Tutorial tutorial = new Liu_Tutorial();
        tutorial.showTutorial2();
    }

    public List<StateEnum> getDrivers(StateEnum liu) {
        drivers.clear();
        Iterator<Map.Entry<StateEnum, StateEnum>> iterator = drivingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<StateEnum, StateEnum> e = iterator.next();
            if (e.getValue() == liu) {
                drivers.add(e.getKey());
            }
        }
        return drivers;
    }

    public StateEnum getDriving(StateEnum liu) {
        if (liu == StateEnum.None) {
            return StateEnum.None;
        }
        return drivingMap.getOrDefault(liu, StateEnum.None);
    }

    public boolean isDriving(StateEnum liu) {
        Iterator<StateEnum> i = drivingMap.values().iterator();
        while (i.hasNext()) {
            StateEnum _liu = i.next();
            if (liu == _liu) {
                return true;
            }
        }
        return false;
    }

    public boolean needRender() {
        return needRender;
    }

    public enum StateEnum {
        None(0), FengZhiLiu(1), XiaZhiLiu(2),
        YuZhiLiu(4), YanZhiLiu(8), All(-1);

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
            } else {
                Utils.addToBotAbstract(() -> {
                    if (AbstractDungeon.player.hasPower(getPowerID())) {
                        AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, getPowerID()));
                    }
                });
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

    class YanZhiLiu_State extends State {
        @Override
        public String getPowerID() {
            return YanZhiLiu.POWER_ID;
        }

        @Override
        public void enter() {
            Utils.playerGainPower(new YanZhiLiu(AbstractDungeon.player));
        }
    }
}
