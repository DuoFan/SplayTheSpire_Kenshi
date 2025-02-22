package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;

public class NotifyBaoYanDamageAction extends AbstractGameAction {
    AbstractCreature target;
    DamageInfo info;

    public NotifyBaoYanDamageAction(AbstractCreature _target, DamageInfo _info) {
        target = _target;
        info = _info;
    }

    @Override
    public void update() {
        isDone = true;
        EventManager.getInstance().notifyEvent(EventKey.ON_BAO_YAN_DAMAGE, target, info);
    }
}
