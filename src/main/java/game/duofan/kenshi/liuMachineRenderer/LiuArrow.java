package game.duofan.kenshi.liuMachineRenderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.duofan.common.DF_Sprite;
import game.duofan.common.RectTransform;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.Liu_StateMachine.StateEnum;

public class LiuArrow {
    public StateEnum from;
    public StateEnum to;

    RectTransform rect;

    DF_Sprite normal;
    DF_Sprite driving;

    public void init(StateEnum _from, StateEnum _to, float x, float y, float width, float height, float scale,String normalImgUrl, String drivingIngUrl) {
        from = _from;
        to = _to;

        rect = new RectTransform();
        rect.posX = x;
        rect.posY = y;
        rect.width = width;
        rect.height = height;
        rect.scaleX = rect.scaleY = scale;
        driving = new DF_Sprite(drivingIngUrl, rect);

        rect = new RectTransform();
        rect.posX = x;
        rect.posY = y;
        rect.width = width;
        rect.height = height;
        rect.scaleX = rect.scaleY = scale;
        normal = new DF_Sprite(normalImgUrl, rect);
    }

    public void render(SpriteBatch sb) {
        Liu_StateMachine machine = Liu_StateMachine.getInstance();

        if(driving == null){
            return;
        }

        if (machine.getDriving(from) == to) {
            driving.render(sb);
        } else {
            normal.render(sb);
        }
    }
}
