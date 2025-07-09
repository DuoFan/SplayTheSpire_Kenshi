package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.BlockImpactLineEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedNumberEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.BuPoFa;
import game.duofan.kenshi.power.ZhanYanLuo;
import game.duofan.kenshi.relic.YiQi;

@SpirePatch2(clz = AbstractCreature.class, method = "decrementBlock", paramtypez = {DamageInfo.class, int.class})
public class BuPoFaPatch {
    @SpireInsertPatch(rloc = 193 - 190)
    public static SpireReturn Patch(AbstractCreature __instance, DamageInfo info, int damageAmount) {
        if (__instance.hasPower(BuPoFa.POWER_ID)) {
            if(__instance.currentBlock > 0){
                CardCrawlGame.sound.play("BLOCK_ATTACK");
                __instance.getPower(BuPoFa.POWER_ID).flash();
            }

            if (damageAmount > __instance.currentBlock) {
                damageAmount -= __instance.currentBlock;
                if (Settings.SHOW_DMG_BLOCK) {
                    AbstractDungeon.effectList.add(new BlockedNumberEffect(__instance.hb.cX, __instance.hb.cY + __instance.hb.height / 2.0F, Integer.toString(__instance.currentBlock)));
                }
            } else if (damageAmount == __instance.currentBlock) {
                damageAmount = 0;
                AbstractDungeon.effectList.add(new BlockedWordEffect(__instance, __instance.hb.cX, __instance.hb.cY, __instance.TEXT[1]));
            } else {
                for(int i = 0; i < 18; ++i) {
                    AbstractDungeon.effectList.add(new BlockImpactLineEffect(__instance.hb.cX, __instance.hb.cY));
                }

                if (Settings.SHOW_DMG_BLOCK) {
                    AbstractDungeon.effectList.add(new BlockedNumberEffect(__instance.hb.cX, __instance.hb.cY + __instance.hb.height / 2.0F, Integer.toString(damageAmount)));
                }
                damageAmount = 0;
            }
            return SpireReturn.Return(damageAmount);
        } else {
            return SpireReturn.Continue();
        }
    }
}
