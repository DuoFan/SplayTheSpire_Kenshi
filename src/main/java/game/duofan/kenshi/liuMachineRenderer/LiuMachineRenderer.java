package game.duofan.kenshi.liuMachineRenderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Slot;
import com.megacrit.cardcrawl.core.Settings;
import game.duofan.common.RectTransform;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.Liu_StateMachine.StateEnum;

public class LiuMachineRenderer {

    LiuSlot[] slots;
    LiuArrow[] arrows;

    float offsetX = 130;
    float offsetY = -50;
    float scale = 0.75f;

    public void init() {
        initSlots();
        initArrows();
    }

    void initSlots() {
        slots = new LiuSlot[4];

        for (int i = 0; i < slots.length; i++) {
            slots[i] = new LiuSlot();
        }

        float centerX = Settings.WIDTH / 2f;
        float centerY = Settings.HEIGHT / 2f;

        slots[0].init(Liu_StateMachine.StateEnum.FengZhiLiu, centerX - 300 + offsetX, centerY + 200 * scale + offsetY, scale,
                "img/liuMachine/feng_IN.png", "img/liuMachine/feng_OFF.png", "img/liuMachine/feng_ON.png", "img/powers/fZL/fZLPower84.png");
        slots[1].init(Liu_StateMachine.StateEnum.YuZhiLiu, centerX - 300 + offsetX, centerY - 170 * scale + offsetY, scale,
                "img/liuMachine/yu_IN.png", "img/liuMachine/yu_OFF.png", "img/liuMachine/yu_ON.png", "ExampleModResources/img/powers/Example84.png");
        slots[2].init(Liu_StateMachine.StateEnum.XiaZhiLiu, slots[0].rect.posX - 130 * scale, (slots[0].rect.posY + slots[1].rect.posY) * 0.5f, scale,
                "img/liuMachine/xia_IN.png", "img/liuMachine/xia_OFF.png", "img/liuMachine/xia_ON.png", "img/powers/xZL/XiaZLPower84.png");
        slots[3].init(Liu_StateMachine.StateEnum.YanZhiLiu, slots[0].rect.posX + 130 * scale, (slots[0].rect.posY + slots[1].rect.posY) * 0.5f, scale,
                "img/liuMachine/fire_IN.png", "img/liuMachine/fire_OFF.png", "img/liuMachine/fire_ON.png", "img/powers/yanZL/yanZL_84.png");
    }

    void initArrows() {
        arrows = new LiuArrow[16];

        for (int i = 0; i < arrows.length; i++) {
            arrows[i] = new LiuArrow();
        }

        StateEnum from;
        StateEnum to;
        LiuSlot fromSlot;
        RectTransform fromRect;
        LiuSlot toSlot;
        RectTransform toRect;

        //枫之流
        {
            from = Liu_StateMachine.StateEnum.FengZhiLiu;
            to = Liu_StateMachine.StateEnum.FengZhiLiu;
            fromSlot = findSlot(from);
            fromRect = fromSlot.rect;

            arrows[0].init(from, to, fromRect.posX + 5 * scale, fromRect.posY + (fromRect.height + 85) * scale * 0.5F,
                    scale, "img/liuMachine/return_feng_OFF.png", "img/liuMachine/return_feng_ON.png");

            to = StateEnum.XiaZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;

            arrows[1].init(from, to, (fromRect.posX + toRect.posX - 20) * 0.5f, (fromRect.posY + toRect.posY + 10) * 0.5f,
                    scale, "img/liuMachine/LD2_OFF.png", "img/liuMachine/LD2_ON.png");

            to = StateEnum.YuZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;

            arrows[2].init(from, to, (fromRect.posX + toRect.posX - 30) * 0.5f, (fromRect.posY + toRect.posY) * 0.5f,
                    scale, "img/liuMachine/CD_OFF.png", "img/liuMachine/CD_ON.png");

            to = StateEnum.YanZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;

            arrows[3].init(from, to, (fromRect.posX + toRect.posX - 20) * 0.5f, (fromRect.posY + toRect.posY - 10) * 0.5f,
                    scale, "img/liuMachine/LU1_OFF.png", "img/liuMachine/LU1_ON.png");
        }

        //霞之流
        {
            from = StateEnum.XiaZhiLiu;
            to = StateEnum.XiaZhiLiu;
            fromSlot = findSlot(from);
            fromRect = fromSlot.rect;

            arrows[4].init(from, to, fromRect.posX - (fromRect.width + 85) * scale * 0.5f, fromRect.posY + 5 * scale,
                    scale, "img/liuMachine/return_xia_OFF.png", "img/liuMachine/return_xia_ON.png");

            to = StateEnum.FengZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;

            arrows[5].init(from, to, (fromRect.posX + toRect.posX + 20) * 0.5f, (fromRect.posY + toRect.posY - 10) * 0.5f,
                    scale, "img/liuMachine/LD1_OFF.png", "img/liuMachine/LD1_ON.png");

            to = StateEnum.YuZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;

            arrows[6].init(from, to, (fromRect.posX + toRect.posX - 20) * 0.5f, (fromRect.posY + toRect.posY - 10) * 0.5f,
                    scale, "img/liuMachine/RD2_OFF.png", "img/liuMachine/RD2_ON.png");

            to = StateEnum.YanZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;

            arrows[7].init(from, to, (fromRect.posX + toRect.posX) * 0.5f, (fromRect.posY + toRect.posY + 20) * 0.5f,
                    scale, "img/liuMachine/CL_OFF.png", "img/liuMachine/CL_ON.png");
        }

        //羽之流
        {
            from = StateEnum.YuZhiLiu;
            to = StateEnum.YuZhiLiu;
            fromSlot = findSlot(from);
            fromRect = fromSlot.rect;

            arrows[8].init(from, to, fromRect.posX + 5 * scale, fromRect.posY - (fromRect.height + 85) * scale * 0.5F,
                    scale, "img/liuMachine/return_yu_OFF.png", "img/liuMachine/return_yu_ON.png");

            to = StateEnum.FengZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;
            arrows[9].init(from, to, (fromRect.posX + toRect.posX + 30) * 0.5f, (fromRect.posY + toRect.posY) * 0.5f,
                    scale, "img/liuMachine/CU_OFF.png", "img/liuMachine/CU_ON.png");

            to = StateEnum.XiaZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;
            arrows[10].init(from, to, (fromRect.posX + toRect.posX + 20) * 0.5f, (fromRect.posY + toRect.posY + 10) * 0.5f,
                    scale, "img/liuMachine/RD1_OFF.png", "img/liuMachine/RD1_ON.png");

            to = StateEnum.YanZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;
            arrows[11].init(from, to, (fromRect.posX + toRect.posX + 20) * 0.5f, (fromRect.posY + toRect.posY - 10) * 0.5f,
                    scale, "img/liuMachine/RU2_OFF.png", "img/liuMachine/RU2_ON.png");
        }

        //炎之流
        {
            from = StateEnum.YanZhiLiu;
            to = StateEnum.YanZhiLiu;
            fromSlot = findSlot(from);
            fromRect = fromSlot.rect;

            arrows[12].init(from, to, fromRect.posX + (fromRect.width + 85) * scale * 0.5f, fromRect.posY + 5 * scale,
                    scale, "img/liuMachine/return_fire_OFF.png", "img/liuMachine/return_fire_ON.png");

            to = StateEnum.FengZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;
            arrows[13].init(from, to, (fromRect.posX + toRect.posX + 20) * 0.5f, (fromRect.posY + toRect.posY + 10) * 0.5f,
                    scale, "img/liuMachine/LU2_OFF.png", "img/liuMachine/LU2_ON.png");

            to = StateEnum.XiaZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;
            arrows[14].init(from, to, (fromRect.posX + toRect.posX) * 0.5f, (fromRect.posY + toRect.posY - 20) * 0.5f,
                    scale, "img/liuMachine/CR_OFF.png", "img/liuMachine/CR_ON.png");

            to = StateEnum.YuZhiLiu;
            toSlot = findSlot(to);
            toRect = toSlot.rect;
            arrows[15].init(from, to, (fromRect.posX + toRect.posX - 20) * 0.5f, (fromRect.posY + toRect.posY + 10) * 0.5f,
                    scale, "img/liuMachine/RU1_OFF.png", "img/liuMachine/RU1_ON.png");
        }
    }

    LiuSlot findSlot(StateEnum liu) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].liu == liu) {
                return slots[i];
            }
        }

        return null;
    }

    public void render(SpriteBatch sb) {
        for (int i = 0; i < slots.length; i++) {
            slots[i].render(sb);
        }
        for (int i = 0; i < arrows.length; i++) {
            arrows[i].render(sb);
        }
    }
}
