package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.Random;

public class EnterRandomLiuAction extends AbstractGameAction {

    public EnterRandomLiuAction() {

    }

    public void update() {
        isDone = true;
        Liu_StateMachine.StateEnum curLiu = Liu_StateMachine.getInstance().getLiu();
        Liu_StateMachine.StateEnum randomLiu = curLiu;
        while (randomLiu == curLiu) {
            int i = AbstractDungeon.cardRandomRng.random(3);

            randomLiu = Liu_StateMachine.StateEnum.None;

            switch (i) {
                case 0:
                    randomLiu = Liu_StateMachine.StateEnum.FengZhiLiu;
                    break;
                case 1:
                    randomLiu = Liu_StateMachine.StateEnum.XiaZhiLiu;
                    break;
                case 2:
                    randomLiu = Liu_StateMachine.StateEnum.YuZhiLiu;
                    break;
                case 3:
                    randomLiu = Liu_StateMachine.StateEnum.YanZhiLiu;
                    break;
            }
        }

        Liu_StateMachine.StateEnum fLiu = randomLiu;
        Utils.addToTopAbstract(() ->{
            Liu_StateMachine.getInstance().changeLiu(fLiu);
        });
    }
}
