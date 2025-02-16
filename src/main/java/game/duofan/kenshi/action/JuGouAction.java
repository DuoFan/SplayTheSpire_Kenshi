package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.DZL_JuGou;

import java.util.ArrayList;
import java.util.Arrays;

public class JuGouAction extends AbstractGameAction {

    DZL_JuGou self;

    public JuGouAction(DZL_JuGou _self) {
        self = _self;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.player.drawPile.size() + AbstractDungeon.player.discardPile.size() == 0) {
                this.isDone = true;
                return;
            }

            if (AbstractDungeon.player.drawPile.isEmpty()) {
                this.addToTop(new JuGouAction(self));
                this.addToTop(new EmptyDeckShuffleAction());
                this.isDone = true;
                return;
            }

            if (!AbstractDungeon.player.drawPile.isEmpty()) {
                AbstractCard card = AbstractDungeon.player.drawPile.getTopCard();
                AbstractDungeon.getCurrRoom().souls.remove(card);
                AbstractDungeon.player.limbo.group.add(card);
                card.current_y = -200.0F * Settings.scale;
                card.target_x = (float) Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
                card.target_y = (float) Settings.HEIGHT / 2.0F;
                card.targetAngle = 0.0F;
                card.lighten(false);
                card.drawScale = 0.12F;
                card.targetDrawScale = 0.75F;
                card.applyPowers();
                card.exhaust = true;
                this.addToTop(new ExhaustSpecificCardAction(card,AbstractDungeon.player.drawPile));
                this.addToTop(new UnlimboAction(card));
                if (!Settings.FAST_MODE) {
                    this.addToTop(new WaitAction(Settings.ACTION_DUR_MED));
                } else {
                    this.addToTop(new WaitAction(Settings.ACTION_DUR_FASTER));
                }
            }

            ArrayList<AbstractCard> cards = getEffectableCards();

            for (int i = 0; i < cards.size(); i++) {
                AbstractCard c = cards.get(i);
                addToBot(new DuanZaoAction(c, 1));
            }

            self.successAmount = cards.size();

            isDone = true;
        }
    }

    ArrayList<AbstractCard> getEffectableCards() {
        ArrayList<AbstractCard> result = Utils.getCardsFromHand(0);

        for (int i = result.size() - 1; i >= 0; i--) {
            AbstractCard c = result.get(i);

            if (c.type != AbstractCard.CardType.ATTACK
                    && c.type != AbstractCard.CardType.SKILL
                    && c.type != AbstractCard.CardType.POWER) {
                result.remove(i);
                continue;
            }

            if (c.type == AbstractCard.CardType.SKILL && c.selfRetain) {
                result.remove(i);
                continue;
            }

            if (c.type == AbstractCard.CardType.POWER && c.cost <= 0) {
                result.remove(i);
                continue;
            }
        }

        return result;
    }
}
