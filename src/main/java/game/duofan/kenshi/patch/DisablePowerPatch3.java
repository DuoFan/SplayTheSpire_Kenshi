package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.*;
import game.duofan.kenshi.power.DisablePowerManager;

import java.util.Iterator;

@SpirePatch2(clz = RemoveSpecificPowerAction.class, method = "update")
public class DisablePowerPatch3 {
    @SpireInsertPatch(rloc = 42 - 36)
    public static SpireReturn Patch(RemoveSpecificPowerAction __instance, String ___powerToRemove,
                                    AbstractPower ___powerInstance) {
        AbstractPower removeMe = DisablePowerManager.getInstance().findDisablePower(__instance.target, ___powerToRemove);

        if (removeMe == null && ___powerInstance != null) {
            removeMe = DisablePowerManager.getInstance().findDisablePower(__instance.target, ___powerInstance.ID);
        }

        if (removeMe == null) {
            return SpireReturn.Continue();
        }

        AbstractDungeon.effectList.add(new PowerExpireTextEffect(__instance.target.hb.cX - __instance.target.animX, __instance.target.hb.cY + __instance.target.hb.height / 2.0F, removeMe.name, removeMe.region128));
        removeMe.onRemove();
        __instance.target.powers.remove(removeMe);
        AbstractDungeon.onModifyPower();
        Iterator var2 = AbstractDungeon.player.orbs.iterator();

        while (var2.hasNext()) {
            AbstractOrb o = (AbstractOrb) var2.next();
            o.updateDescription();
        }

        return SpireReturn.Return();
    }
}
