package game.duofan.kenshi.patch;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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
