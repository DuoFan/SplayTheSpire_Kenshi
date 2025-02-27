package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class DrawCardByClassAction extends AbstractGameAction {
    private final Class<?> targetClass;  // 改用 Class<?> 类型

    IDoCard doCard;

    public DrawCardByClassAction(int amount, Class<?> targetClass) {
        this.amount = amount;
        this.targetClass = targetClass;
    }

    public DrawCardByClassAction(int amount, Class<?> targetClass, IDoCard doCard) {
        this.amount = amount;
        this.targetClass = targetClass;
        this.doCard = doCard;
    }

    @Override
    public void update() {
        if (targetClass != null) {
            DrawCardByFilterAction a = new DrawCardByFilterAction(amount, (c) -> {
                return targetClass.isInstance(c);
            });
            a.setDoCard(doCard);
            addToTop(a);
        }
        this.isDone = true;
    }
}