package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.unique.UnloadAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.FeiGong;
import game.duofan.kenshi.card.ShuangJianHeBi_Card;
import game.duofan.kenshi.power.ShuangJianHeBi;

import java.util.Iterator;

public class ShuangJianHeBiAction extends AbstractGameAction implements ICardFilter, IDoCard {

    AbstractCard c1;
    AbstractCard c2;
    ShuangJianHeBi_Card self;

    public ShuangJianHeBiAction(ShuangJianHeBi_Card _self) {
        self = _self;
    }

    public void update() {
        isDone = true;

        if(self.upgraded){
            Utils.addToBotAbstract(() -> {
                if (c1 == null) {
                    return;
                }
                if (c2 == null) {
                    return;
                }
                Utils.playerGainPowerTop(new ShuangJianHeBi(AbstractDungeon.player, c1, c2));
            });
        }

        DrawCardByFilterAction a = new DrawCardByFilterAction(2, this);
        a.setDoCard(this);
        addToTop(a);
    }

    @Override
    public boolean filter(AbstractCard c) {
        return c.type == AbstractCard.CardType.ATTACK;
    }

    @Override
    public void DoCard(AbstractCard card) {
        if (c1 == null) {
            c1 = card;
        } else if (c2 == null) {
            c2 = card;
        }
    }
}
