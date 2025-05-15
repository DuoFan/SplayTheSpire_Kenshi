package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.kenshi.power.DisablePower;
import game.duofan.kenshi.power.DisablePowerManager;

@SpirePatch2(clz = ApplyPowerAction.class, method = "update")
public class DisablePowerPatch1 {
    @SpireInsertPatch(rloc = 125 - 122)
    public static SpireReturn Patch(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
        if (!(___powerToApply instanceof DisablePower)) {
            return SpireReturn.Continue();
        }

        DisablePower disablePower = (DisablePower) ___powerToApply;
        AbstractPower originPower = disablePower.getOriginPower();

        AbstractCreature owner = originPower.owner;
        if (owner == null) {
            return SpireReturn.Continue();
        }

        __instance.isDone = true;

        if (owner instanceof AbstractMonster && owner.isDeadOrEscaped()) {
            return SpireReturn.Return();
        }

        int index = owner.powers.indexOf(originPower);
        if(index >= 0){
            owner.powers.set(index, disablePower);
        }

        DisablePowerManager.getInstance().addDisablePower(disablePower);

        AbstractDungeon.onModifyPower();

        return SpireReturn.Return();
    }
}
