package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.Liu_StateMachine.StateEnum;

import javax.swing.*;

public class OthersDriveLiuAction extends AbstractGameAction {

    StateEnum target;

    public OthersDriveLiuAction(StateEnum _target) {
        target = _target;
    }

    @Override
    public void update() {
        isDone = true;
        tryDriveLiu(StateEnum.FengZhiLiu, target);
        tryDriveLiu(StateEnum.XiaZhiLiu, target);
        tryDriveLiu(StateEnum.YuZhiLiu, target);
        tryDriveLiu(StateEnum.YanZhiLiu, target);
    }

    void tryDriveLiu(StateEnum from, StateEnum to) {
        if (from == to) {
            return;
        }

        Liu_StateMachine.getInstance().setDriving(from, to);
    }
}
