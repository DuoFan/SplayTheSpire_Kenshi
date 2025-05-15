package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.*;
import game.duofan.kenshi.power.DisablePower;
import game.duofan.kenshi.power.DisablePowerManager;

@SpirePatch2(clz = ApplyPowerAction.class, method = "update")
public class DisablePowerPatch2 {
    @SpireInsertPatch(rloc = 173 - 122)
    public static SpireReturn Patch(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
        DisablePower disablePower = DisablePowerManager.getInstance().findDisablePower(___powerToApply);

        if (disablePower == null) {
            return SpireReturn.Continue();
        }

        if (disablePower.amount == -1 && !(___powerToApply instanceof StrengthPower)
                && !(___powerToApply instanceof DexterityPower) && !(___powerToApply instanceof FocusPower)) {
            return SpireReturn.Continue();
        }

        disablePower.stackPower(___powerToApply.amount);

        __instance.isDone = true;
        AbstractDungeon.onModifyPower();

        return SpireReturn.Return();
    }
}
