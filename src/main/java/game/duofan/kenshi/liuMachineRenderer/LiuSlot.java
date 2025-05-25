package game.duofan.kenshi.liuMachineRenderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.duofan.common.DF_Sprite;
import game.duofan.common.RectTransform;
import game.duofan.kenshi.power.Liu_StateMachine;

public class LiuSlot {
    public Liu_StateMachine.StateEnum liu;
    public RectTransform rect;

    DF_Sprite normal;
    DF_Sprite driving;
    DF_Sprite power;

    public void init(Liu_StateMachine.StateEnum _liu, float x, float y, float scale, String normalImgUrl, String drivingIngUrl, String powerImgUrl) {
        liu = _liu;

        rect = new RectTransform();
        rect.posX = x;
        rect.posY = y;
        rect.scaleX = rect.scaleY = scale;
        power = new DF_Sprite(powerImgUrl, rect);
        power.useTextureSize = true;

        rect = new RectTransform();
        rect.posX = x;
        rect.posY = y;
        rect.width = rect.height = 168;
        rect.scaleX = rect.scaleY = scale;
        driving = new DF_Sprite(drivingIngUrl, rect);

        rect = new RectTransform();
        rect.posX = x;
        rect.posY = y;
        rect.width = rect.height = 122;
        rect.scaleX = rect.scaleY = scale;
        normal = new DF_Sprite(normalImgUrl, rect);
    }

    public void render(SpriteBatch sb) {
        Liu_StateMachine machine = Liu_StateMachine.getInstance();
        if (machine.isDriving(liu)) {
            driving.render(sb);
        } else {
            normal.render(sb);
        }
        power.render(sb);
    }
}
