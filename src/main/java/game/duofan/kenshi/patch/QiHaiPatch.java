package game.duofan.kenshi.patch;

import basemod.devcommands.relic.Relic;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.*;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.XZL_QiHai;
import game.duofan.kenshi.card.YuZL_YuYi;
import game.duofan.kenshi.power.BuPoFa;
import game.duofan.kenshi.power.Qi;
import game.duofan.kenshi.power.ZhanYanLuo;
import game.duofan.kenshi.relic.YiQi;

import java.util.Iterator;

@SpirePatch2(clz = GameActionManager.class, method = "callEndOfTurnActions")
public class QiHaiPatch {
    @SpireInsertPatch(rloc = 0)
    public static void Patch() {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractPower qiPower = p.getPower(Qi.POWER_ID);
        if (qiPower == null || qiPower.amount < 0) {
            return;
        }

        CardGroup hand = p.hand;
        if (hand == null) {
            return;
        }

        for (int i = 0; i < hand.size(); i++) {
            AbstractCard c = hand.group.get(i);

            if (c instanceof XZL_QiHai) {
                int max = c.upgraded ? XZL_QiHai.UPGRADED_COST : XZL_QiHai.COST;
                if (c.cost > max) {
                    c.modifyCostForCombat(max - c.cost);
                    c.superFlash();
                }
                if (c.cost == max) {
                    continue;
                }

                while (qiPower.amount > 0 && c.cost < max) {
                    qiPower.amount--;
                    c.modifyCostForCombat(1);
                }
                c.costForTurn = c.cost;
                c.superFlash();
            }
        }
    }
}
