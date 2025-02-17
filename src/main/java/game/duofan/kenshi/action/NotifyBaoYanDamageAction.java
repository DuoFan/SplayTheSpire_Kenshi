package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.kenshi.card.DZL_JuGou;

import java.util.ArrayList;

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
