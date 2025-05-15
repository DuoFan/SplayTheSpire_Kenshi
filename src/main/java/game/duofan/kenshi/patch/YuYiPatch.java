package game.duofan.kenshi.patch;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.*;
import game.duofan.kenshi.card.YuYi;

import java.util.Iterator;

@SpirePatch2(clz = AbstractPlayer.class, method = "damage", paramtypez = {DamageInfo.class})
public class YuYiPatch {
    @SpireInsertPatch(rloc = 1797 - 1727)
    public static SpireReturn Patch(AbstractPlayer __instance, DamageInfo info, int ___damageAmount) {
        if (___damageAmount <= 0) {
            return SpireReturn.Continue();
        }

        CardGroup hand = __instance.hand;

        if (hand == null) {
            return SpireReturn.Continue();
        }

        int yuYiBlock = 0;
        for (int i = 0; i < hand.size(); i++) {
            AbstractCard c = hand.group.get(i);
            if (c.cost > 0 && c instanceof YuYi) {
                yuYiBlock = c.cost;
            }
        }

        if (yuYiBlock <= 0) {
            return SpireReturn.Continue();
        }

        boolean playedSound = false;

        for (int i = 0; i < hand.size() && ___damageAmount > 0; i++) {
            AbstractCard c = hand.group.get(i);
            if (c.cost > 0 && c instanceof YuYi) {
                int b = 0;
                while (c.cost > 0 && ___damageAmount > 0) {
                    c.modifyCostForCombat(-1);
                    ___damageAmount--;
                    b++;
                }
                if(b > 0){
                    c.superFlash();
                    AbstractDungeon.effectList.add(new BlockedNumberEffect(c.current_x + -132.0F * c.drawScale * Settings.scale, c.current_y + 220.0F * c.drawScale * Settings.scale, Integer.toString(b)));
                    if(!playedSound){
                        playedSound = true;
                        CardCrawlGame.sound.play("BLOCK_ATTACK");
                    }
                }
            }
        }

        __instance.lastDamageTaken = Math.min(___damageAmount, __instance.currentHealth);

        if(___damageAmount <= 0){
            return SpireReturn.Return();
        }

        AbstractPower p;
        for(Iterator<AbstractPower> iP = __instance.powers.iterator(); iP.hasNext(); ___damageAmount = p.onLoseHp(___damageAmount)) {
            p = (AbstractPower)iP.next();
        }

        Iterator<AbstractRelic> iR = __instance.relics.iterator();

        while(iR.hasNext()) {
            AbstractRelic r = (AbstractRelic)iR.next();
            r.onLoseHp(___damageAmount);
        }

        Iterator<AbstractPower> iP = __instance.powers.iterator();

        while (iP.hasNext()) {
            p = (AbstractPower) iP.next();
            p.wasHPLost(info, ___damageAmount);
        }

        Iterator<AbstractRelic> rP = __instance.relics.iterator();

        while (rP.hasNext()) {
            AbstractRelic r = (AbstractRelic) rP.next();
            r.wasHPLost(___damageAmount);
        }

        if (info.owner != null) {
            Iterator<AbstractPower> iP2 = info.owner.powers.iterator();

            while (iP2.hasNext()) {
                p = (AbstractPower) iP2.next();
                p.onInflictDamage(info, ___damageAmount, __instance);
            }
        }

        if (info.owner != __instance) {
            __instance.useStaggerAnimation();
        }

        if (info.type == DamageInfo.DamageType.HP_LOSS) {
            GameActionManager.hpLossThisCombat += ___damageAmount;
        }
        GameActionManager.damageReceivedThisTurn += ___damageAmount;
        GameActionManager.damageReceivedThisCombat += ___damageAmount;

        __instance.currentHealth -= ___damageAmount;
        if (___damageAmount > 0 && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            //__instance.updateCardsOnDamage();
            updateCardsOnDamage(__instance);
            ++__instance.damagedThisCombat;
        }

        AbstractDungeon.effectList.add(new StrikeEffect(__instance, __instance.hb.cX, __instance.hb.cY, ___damageAmount));
        if (__instance.currentHealth < 0) {
            __instance.currentHealth = 0;
        } else if (__instance.currentHealth < __instance.maxHealth / 4) {
            AbstractDungeon.topLevelEffects.add(new BorderFlashEffect(new Color(1.0F, 0.1F, 0.05F, 0.0F)));
        }

        __instance.healthBarUpdatedEvent();
        if ((float) __instance.currentHealth <= (float) __instance.maxHealth / 2.0F && !__instance.isBloodied) {
            __instance.isBloodied = true;
            Iterator<AbstractRelic> i = __instance.relics.iterator();

            while (i.hasNext()) {
                AbstractRelic r = i.next();
                if (r != null) {
                    r.onBloodied();
                }
            }
        }

        if (__instance.currentHealth < 1) {
            if (!__instance.hasRelic("Mark of the Bloom")) {
                if (__instance.hasPotion("FairyPotion")) {
                    Iterator<AbstractPotion> i = __instance.potions.iterator();

                    while (i.hasNext()) {
                        AbstractPotion po = (AbstractPotion) i.next();
                        if (po.ID.equals("FairyPotion")) {
                            po.flash();
                            __instance.currentHealth = 0;
                            po.use(__instance);
                            AbstractDungeon.topPanel.destroyPotion(po.slot);
                            return SpireReturn.Return();
                        }
                    }
                } else if (__instance.hasRelic("Lizard Tail") && ((LizardTail) __instance.getRelic("Lizard Tail")).counter == -1) {
                    __instance.currentHealth = 0;
                    __instance.getRelic("Lizard Tail").onTrigger();
                    return SpireReturn.Return();
                }
            }

            float BLOCK_ICON_X = -14.0F * Settings.scale;
            float BLOCK_ICON_Y = -14.0F * Settings.scale;

            __instance.isDead = true;
            AbstractDungeon.deathScreen = new DeathScreen(AbstractDungeon.getMonsters());
            __instance.currentHealth = 0;
            if (__instance.currentBlock > 0) {
                __instance.loseBlock();
                AbstractDungeon.effectList.add(new HbBlockBrokenEffect(__instance.hb.cX - __instance.hb.width / 2.0F + BLOCK_ICON_X, __instance.hb.cY - __instance.hb.height / 2.0F + BLOCK_ICON_Y));
            }
        }

        return SpireReturn.Return();
    }

    private static void updateCardsOnDamage(AbstractPlayer p) {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            Iterator var1 = p.hand.group.iterator();

            AbstractCard c;
            while (var1.hasNext()) {
                c = (AbstractCard) var1.next();
                c.tookDamage();
            }

            var1 = p.discardPile.group.iterator();

            while (var1.hasNext()) {
                c = (AbstractCard) var1.next();
                c.tookDamage();
            }

            var1 = p.drawPile.group.iterator();

            while (var1.hasNext()) {
                c = (AbstractCard) var1.next();
                c.tookDamage();
            }
        }

    }
}
