package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;

public class RepeatAction extends AbstractGameAction {
    AbstractGameAction action;
    int count;
    Utils.Lambda lambda;

    public RepeatAction(AbstractGameAction a,int c){
        action = a;
        count = c;
    }

    public RepeatAction(Utils.Lambda l,int c){
        lambda = l;
        count = c;
    }

    @Override
    public void update() {
        if (count > 0){
            System.out.println("执行---" + count);
            if(action != null){
                AbstractDungeon.actionManager.addToTop(action);
                action.update();
                action.isDone = false;
            }
            else if(lambda != null){
                AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                    @Override
                    public void update() {
                        lambda.run();
                    }
                });
            }
            count--;
        }
        this.isDone = true;
    }

}
