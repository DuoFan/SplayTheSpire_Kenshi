package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.kenshi.card.QiHai;
import game.duofan.kenshi.power.Qi;

@SpirePatch2(clz = GameActionManager.class, method = "callEndOfTurnActions")
public class QiHaiPatch {
    @SpireInsertPatch(rloc = 0)
    public static void Patch() {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractPower qiPower = p.getPower(Qi.POWER_ID);
        if (qiPower == null || qiPower.amount < 0) {
            return;
        }

        CardGroup hand = p.hand;
        if (hand == null) {
            return;
        }

        for (int i = 0; i < hand.size(); i++) {
            AbstractCard c = hand.group.get(i);

            if (c instanceof QiHai) {
                int max = c.upgraded ? QiHai.UPGRADED_COST : QiHai.COST;
                if (c.cost > max) {
                    c.modifyCostForCombat(max - c.cost);
                    c.superFlash();
                }
                if (c.cost == max) {
                    continue;
                }

                while (qiPower.amount > 0 && c.cost < max) {
                    qiPower.amount--;
                    c.modifyCostForCombat(1);
                }
                c.costForTurn = c.cost;
                c.superFlash();
            }
        }
    }
}
