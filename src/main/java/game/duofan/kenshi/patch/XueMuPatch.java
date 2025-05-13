package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.XueMu;
import game.duofan.kenshi.power.ZhanYanLuo;
import game.duofan.kenshi.relic.YiQi;

@SpirePatch2(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
public class XueMuPatch {
    @SpireInsertPatch(rloc = 0)
    public static void Patch(AbstractMonster __instance) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return;
        }

        AbstractPower power = p.getPower(XueMu.POWER_ID);

        if (power != null && power.amount > 0) {
            Utils.gainHeal(p, power.amount);
        }
    }
}
