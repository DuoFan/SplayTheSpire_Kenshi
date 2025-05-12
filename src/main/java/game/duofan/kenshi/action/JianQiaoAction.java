package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import game.duofan.common.Utils;

public class JianQiaoAction extends AbstractGameAction {

    CardGroup g;
    AbstractMonster m;

    public JianQiaoAction(CardGroup _g, AbstractMonster _m) {
        g = _g;
        m = _m;
    }

    public void update() {
        isDone = true;
        if (g == null) {
            return;
        }
        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.getNCardFromTop(i);
            c.targetDrawScale = 1;
            addToTop(new NewQueueCardAction(c, m, true, true));
        }
        g.clear();
    }
}
