package game.duofan.kenshi.liuMachineRenderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.DF_Sprite;
import game.duofan.common.RectTransform;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.Liu_StateMachine.StateEnum;

public class LiuArrow {
    public StateEnum from;
    public StateEnum to;

    RectTransform rect;

    DF_Sprite normal;
    DF_Sprite driving;

    public void init(StateEnum _from, StateEnum _to, float x, float y, float scale, String normalImgUrl, String drivingIngUrl) {
        from = _from;
        to = _to;

        rect = new RectTransform();
        rect.posX = x;
        rect.posY = y;
        rect.scaleX = rect.scaleY = scale;
        driving = new DF_Sprite(drivingIngUrl, rect);
        driving.useTextureSize = true;

        rect = new RectTransform();
        rect.posX = x;
        rect.posY = y;
        rect.scaleX = rect.scaleY = scale;
        normal = new DF_Sprite(normalImgUrl, rect);
        normal.useTextureSize = true;
    }

    public void render(SpriteBatch sb) {
        Liu_StateMachine machine = Liu_StateMachine.getInstance();

        if (driving == null) {
            return;
        }

        if (machine.getDriving(from) == to) {
            if (machine.getExchangable(from).indexOf(to) >= 0) {
                driving.color.r = 0;
                driving.color.g = 0;
                driving.color.b = 1;
                driving.color.a = 1;
                driving.render(sb);
            } else {
                driving.color.r = driving.color.g = driving.color.b = driving.color.a = 1;
                driving.render(sb);
            }
        } else if (machine.getLiu() == from) {
            if (machine.getExchangable(from).indexOf(to) >= 0) {
                driving.color.r = 0;
                driving.color.g = 0;
                driving.color.b = 1;
                driving.color.a = 1;
                driving.render(sb);
            } else if (AbstractDungeon.player.hoveredCard != null && machine.getInvokeable(from).indexOf(to) >= 0) {
                driving.color.r = driving.color.g = driving.color.b = 1;
                driving.color.a = 0.8f;
                driving.render(sb);
            }
            else {
                normal.render(sb);
            }
        } else {
            normal.render(sb);
        }
    }
}
