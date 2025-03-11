package game.duofan.kenshi.patch;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.kenshi.power.FanShi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SpirePatch2(clz = DrawCardAction.class, method = "update")
public class DrawCardActionPatch {
    @SpireInsertPatch(rloc = 88 - 68, localvars = {"discardSize"})
    public static SpireReturn InstertPatch(DrawCardAction __instance, int discardSize) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null && p.hasPower(FanShi.POWER_ID)) {
            int tmp;
            if (__instance.amount + AbstractDungeon.player.hand.size() > 10) {
                tmp = 10 - (__instance.amount + AbstractDungeon.player.hand.size());
                __instance.amount += tmp;
                AbstractDungeon.player.createHandIsFullDialog();
            }

            if (__instance.amount > discardSize) {
                __instance.amount = discardSize;
            }

            if (__instance.amount > 0) {
                Field f = AbstractGameAction.class.getDeclaredField("duration");
                f.setAccessible(true);
                float duration = f.getFloat(__instance);
                duration -= Gdx.graphics.getDeltaTime();
                f.setFloat(__instance, duration);
                if (duration < 0.0F) {
                    if (Settings.FAST_MODE) {
                        f.setFloat(__instance, Settings.ACTION_DUR_XFAST);
                    } else {
                        f.setFloat(__instance, Settings.ACTION_DUR_FASTER);
                    }

                    --__instance.amount;
                    if (!AbstractDungeon.player.discardPile.isEmpty()) {
                        __instance.drawnCards.add(AbstractDungeon.player.discardPile.getTopCard());
                        AbstractDungeon.player.draw();
                        AbstractDungeon.player.hand.refreshHandLayout();
                        if (__instance.amount == 0) {
                            endActionWithFollowUp(__instance);
                        }
                    } else {
                        endActionWithFollowUp(__instance);
                    }
                }
                f.setAccessible(false);
            } else {
                endActionWithFollowUp(__instance);
            }
            return SpireReturn.Return();
        } else {
            return SpireReturn.Continue();
        }
    }

    static void endActionWithFollowUp(DrawCardAction __instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = __instance.getClass().getDeclaredMethod("endActionWithFollowUp");
        m.setAccessible(true);
        m.invoke(__instance);
        m.setAccessible(false);
    }
}
