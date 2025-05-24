package game.duofan.kenshi.patch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.*;
import game.duofan.kenshi.card.YuYi;
import game.duofan.kenshi.power.FeiYing;
import game.duofan.kenshi.power.FuYao;

import java.lang.reflect.Field;
import java.util.Iterator;

@SpirePatch2(clz = ApplyPowerAction.class, method = "update")
public class FeiYingPatch {
    @SpireInsertPatch(rloc = 173 - 122)
    public static SpireReturn Patch(ApplyPowerAction __instance,AbstractPower ___powerToApply) throws IllegalAccessException, NoSuchFieldException {
        if (___powerToApply.type != AbstractPower.PowerType.DEBUFF) {
            return SpireReturn.Continue();
        }

        AbstractPower p = __instance.target.getPower(FeiYing.POWER_ID);
        if(p == null){
            return SpireReturn.Continue();
        }

        p = __instance.target.getPower(FuYao.POWER_ID);
        if(p == null || p.amount <= 0){
            return SpireReturn.Continue();
        }

        FuYao fuyao = (FuYao)p;

        Field f = AbstractGameAction.class.getDeclaredField("duration");
        f.setAccessible(true);

        float duration = (Float)f.get(__instance);
        duration -= Gdx.graphics.getDeltaTime();
        f.set(__instance,duration);

        f.setAccessible(false);

        AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(__instance.target, __instance.TEXT[0]));
        CardCrawlGame.sound.play("NULLIFY_SFX");
        fuyao.subAmount(2);

        return SpireReturn.Return();
    }
}
