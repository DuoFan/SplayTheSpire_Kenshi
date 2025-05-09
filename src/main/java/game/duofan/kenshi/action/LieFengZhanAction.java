package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.kenshi.card.FZL_LieFengZhan;

public class LieFengZhanAction extends AbstractGameAction {

    FZL_LieFengZhan card;
    AbstractMonster monster;

    public LieFengZhanAction(FZL_LieFengZhan _card, AbstractMonster _monster, int _amount) {
        card = _card;
        monster = _monster;
        amount = _amount;
    }

    @Override
    public void update() {
        isDone = true;

        FZL_LieFengZhan tmp = (FZL_LieFengZhan)card.makeSameInstanceOf();
        AbstractDungeon.player.limbo.addToBottom(tmp);
        tmp.current_x = card.current_x;
        tmp.current_y = card.current_y;
        tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
        tmp.target_y = (float) Settings.HEIGHT / 2.0F;
        tmp.calculateCardDamage(monster);

        tmp.purgeOnUse = true;
        tmp.duplicate = true;
        AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, monster, card.energyOnUse, true, true), true);
        --this.amount;

        if (amount > 0) {
            addToBot(new LieFengZhanAction(card, monster, amount));
        }
    }
}
