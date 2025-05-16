package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.XueLu;

@SpirePatch2(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
public class XueMuPatch {
    @SpireInsertPatch(rloc = 0)
    public static void Patch(AbstractMonster __instance) {

        if(!Utils.isKilled(__instance)){
            return;
        }

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return;
        }

        AbstractPower power = p.getPower(XueLu.POWER_ID);

        if (power != null && power.amount > 0) {
            Utils.gainHeal(p, power.amount);
        }
    }
}
