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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.NoDrawPower;
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
