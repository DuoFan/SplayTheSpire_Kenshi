package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class DrawCardByClassAction extends AbstractGameAction {
    private final Class<?> targetClass;  // 改用 Class<?> 类型

    public DrawCardByClassAction(int amount, Class<?> targetClass) {
        this.amount = amount;
        this.targetClass = targetClass;
    }

    @Override
    public void update() {
        if (targetClass != null) {
            addToTop(new DrawCardByFilterAction(amount, (c) -> {
                return targetClass.isInstance(c);
            }));
        }
        this.isDone = true;
    }
}