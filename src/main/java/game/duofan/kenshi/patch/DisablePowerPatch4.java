package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.AwakenedOne;
import com.megacrit.cardcrawl.monsters.beyond.Darkling;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.*;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.DisablePowerManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public class DisablePowerPatch4 {

    static SpireReturn TrySkipDisablePower(AbstractCreature target, String powerID) {
        if (CheckDisablePower(target, powerID)) {
            return SpireReturn.Return();
        } else {
            return SpireReturn.Continue();
        }
    }

    static boolean CheckDisablePower(AbstractCreature target, String powerID) {
        if (target == null) {
            return false;
        }

        for (int i = 0; i < target.powers.size(); i++) {
            if (target.powers.get(i).ID.equals(powerID)) {
                return false;
            }
        }

        AbstractPower disabled = DisablePowerManager.getInstance().findDisablePower(target, powerID);

        if (disabled != null) {
            return true;
        } else {
            return false;
        }
    }

    @SpirePatch2(clz = SlimeBoss.class, method = "damage", paramtypez = {DamageInfo.class})
    static class SlimeBossPathc {
        @SpireInsertPatch(rloc = 167 - 164)
        public static SpireReturn Patch(DamageInfo info, SlimeBoss __instance) {
            return TrySkipDisablePower(__instance, SplitPower.POWER_ID);
        }
    }

    @SpirePatch2(clz = SpikeSlime_L.class, method = "damage", paramtypez = {DamageInfo.class})
    static class SpikeSlime_L_Pathc {
        @SpireInsertPatch(rloc = 131 - 128)
        public static SpireReturn Patch(DamageInfo info, SpikeSlime_L __instance) {
            return TrySkipDisablePower(__instance, SplitPower.POWER_ID);
        }
    }

    @SpirePatch2(clz = AcidSlime_L.class, method = "damage", paramtypez = {DamageInfo.class})
    static class AcidSlime_L_Pathc {
        @SpireInsertPatch(rloc = 140 - 137)
        public static SpireReturn Patch(DamageInfo info, AcidSlime_L __instance) {
            return TrySkipDisablePower(__instance, SplitPower.POWER_ID);
        }
    }

    @SpirePatch2(clz = DamageAction.class, method = "stealGold")
    static class Looter_Pathc {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn Patch(DamageAction __instance) throws NoSuchFieldException, IllegalAccessException {
            return TrySkipDisablePower(__instance.source, ThieveryPower.POWER_ID);
        }
    }

    @SpirePatch2(clz = AwakenedOne.class, method = "damage", paramtypez = {DamageInfo.class})
    static class AwakenedOne_Pathc {
        @SpireInsertPatch(rloc = 320 - 308)
        public static SpireReturn Patch(DamageInfo info, AwakenedOne __instance) throws NoSuchFieldException, IllegalAccessException {
            boolean disabled = CheckDisablePower(__instance, UnawakenedPower.POWER_ID);
            if (disabled) {
                AbstractDungeon.getCurrRoom().cannotLose = false;
                __instance.die();
                return SpireReturn.Return();
            } else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch2(clz = Darkling.class, method = "damage", paramtypez = {DamageInfo.class})
    static class Darkling_Pathc {
        @SpireInsertPatch(rloc = 207 - 204)
        public static SpireReturn Patch(DamageInfo info, Darkling __instance) {
            boolean disabled = CheckDisablePower(__instance, RegrowPower.POWER_ID);
            if (disabled) {
                boolean oriState = AbstractDungeon.getCurrRoom().cannotLose;
                AbstractDungeon.getCurrRoom().cannotLose = false;
                __instance.die();
                Utils.addToTopAbstract(() ->{
                    AbstractDungeon.getMonsters().monsters.remove(__instance);
                });
                AbstractDungeon.getCurrRoom().cannotLose = oriState;
                return SpireReturn.Return();
            } else {
                return SpireReturn.Continue();
            }
        }
    }
}
