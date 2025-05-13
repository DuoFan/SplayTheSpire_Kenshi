package game.duofan.kenshi.patch;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.*;
import game.duofan.kenshi.card.YuZL_YuYi;
import game.duofan.kenshi.power.DisablePower;
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
