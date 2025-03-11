package game.duofan.kenshi.patch;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import game.duofan.kenshi.power.FanShi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

@SpirePatch2(clz = AbstractPlayer.class, method = "draw", paramtypez = {int.class})
public class PlayerDrawPatch {
    @SpireInsertPatch(rloc = 2067 - 2067)
    public static SpireReturn InstertPatch(AbstractPlayer __instance, int numCards) {
        if (__instance.hasPower(FanShi.POWER_ID)) {
            for(int i = 0; i < numCards; ++i) {
                if (__instance.discardPile.isEmpty()) {
                    break;
                } else {
                    AbstractCard c = __instance.discardPile.getBottomCard();
                    c.current_x = CardGroup.DISCARD_PILE_X;
                    c.current_y = CardGroup.DISCARD_PILE_Y;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    c.triggerWhenDrawn();
                    __instance.hand.addToHand(c);
                    __instance.discardPile.group.remove(0);
                    Iterator var4 = __instance.powers.iterator();

                    while(var4.hasNext()) {
                        AbstractPower p = (AbstractPower)var4.next();
                        p.onCardDraw(c);
                    }

                    var4 = __instance.relics.iterator();

                    while(var4.hasNext()) {
                        AbstractRelic r = (AbstractRelic)var4.next();
                        r.onCardDraw(c);
                    }
                }
            }
            return SpireReturn.Return();
        } else {
            return SpireReturn.Continue();
        }
    }
}
