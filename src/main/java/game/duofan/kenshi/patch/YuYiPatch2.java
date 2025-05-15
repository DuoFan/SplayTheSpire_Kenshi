package game.duofan.kenshi.patch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import game.duofan.kenshi.card.YuYi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

@SpirePatch2(clz = AbstractCreature.class, method = "addBlock", paramtypez = {int.class})
public class YuYiPatch2 {
    @SpireInsertPatch(rloc = 0)
    public static SpireReturn Patch(AbstractCreature __instance, int blockAmount) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        if (!__instance.isPlayer || !(__instance instanceof AbstractPlayer)) {
            return SpireReturn.Continue();
        }

        if (blockAmount <= 0) {
            return SpireReturn.Continue();
        }

        AbstractPlayer player = (AbstractPlayer) __instance;

        CardGroup hand = player.hand;

        if (hand == null) {
            return SpireReturn.Continue();
        }

        boolean hasYuYi = false;
        for (int i = 0; i < hand.size() && !hasYuYi; i++) {
            AbstractCard c = hand.group.get(i);
            if (c instanceof YuYi) {
                int max = c.upgraded ? YuYi.UPGRADED_COST : YuYi.COST;
                hasYuYi = c.cost < max;
            }
        }

        if (!hasYuYi) {
            return SpireReturn.Continue();
        }

        for (int i = 0; i < hand.size() && blockAmount > 0; i++) {
            AbstractCard c = hand.group.get(i);
            if (c instanceof YuYi) {
                int max = c.upgraded ? YuYi.UPGRADED_COST : YuYi.COST;
                boolean valid = c.cost < max && blockAmount > 0;
                while (c.cost < max && blockAmount > 0) {
                    c.modifyCostForCombat(1);
                    c.costForTurn = c.cost;
                    blockAmount--;
                }
                if (valid) {
                    c.superFlash();
                }
            }
        }

        float tmp = (float) blockAmount;
        Iterator var3;
        AbstractRelic r;
        for (var3 = AbstractDungeon.player.relics.iterator(); var3.hasNext(); tmp = (float) r.onPlayerGainedBlock(tmp)) {
            r = (AbstractRelic) var3.next();
        }

        if (tmp > 0.0F) {
            var3 = __instance.powers.iterator();

            while (var3.hasNext()) {
                AbstractPower p = (AbstractPower) var3.next();
                p.onGainedBlock(tmp);
            }
        }

        boolean effect = false;
        if (__instance.currentBlock == 0) {
            effect = true;
        }

        Iterator var10 = AbstractDungeon.getCurrRoom().monsters.monsters.iterator();

        while (var10.hasNext()) {
            AbstractMonster m = (AbstractMonster) var10.next();

            AbstractPower p;
            for (Iterator var6 = m.powers.iterator(); var6.hasNext(); tmp = (float) p.onPlayerGainedBlock(tmp)) {
                p = (AbstractPower) var6.next();
            }
        }

        __instance.currentBlock += MathUtils.floor(tmp);
        if (__instance.currentBlock >= 99 && __instance.isPlayer) {
            UnlockTracker.unlockAchievement("IMPERVIOUS");
        }

        if (__instance.currentBlock > 999) {
            __instance.currentBlock = 999;
        }

        if (__instance.currentBlock == 999) {
            UnlockTracker.unlockAchievement("BARRICADED");
        }

        Class c = AbstractCreature.class;

        if (effect && __instance.currentBlock > 0) {
            Method m = c.getDeclaredMethod("gainBlockAnimation");
            m.setAccessible(true);
            m.invoke(__instance);
            m.setAccessible(false);
        } else if (blockAmount > 0 && blockAmount > 0) {
            Color tmpCol = Settings.GOLD_COLOR.cpy();

            Field fColor = c.getDeclaredField("blockTextColor");
            Field fScale = c.getDeclaredField("blockScale");

            fColor.setAccessible(true);
            fScale.setAccessible(true);

            Color blockTextColor = (Color) fColor.get(__instance);

            tmpCol.a = blockTextColor.a;

            fColor.set(__instance, tmpCol);
            fScale.set(__instance, 5);

            fColor.setAccessible(false);
            fScale.setAccessible(false);
        }

        return SpireReturn.Return();
    }
}
