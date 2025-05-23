package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EnableEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.BlockImpactLineEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedNumberEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.BuPoFa;
import game.duofan.kenshi.power.Yi;
import game.duofan.kenshi.power.ZhanYanLuo;
import game.duofan.kenshi.relic.YiQi;

@SpirePatch2(clz = GameActionManager.class, method = "getNextAction")
public class YiPatch {
    @SpireInsertPatch(rloc = 438 - 200)
    public static SpireReturn Patch(GameActionManager __instance) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return SpireReturn.Continue();
        }

        if (p.currentBlock <= 0) {
            return SpireReturn.Continue();
        }

        AbstractPower power = p.getPower(Yi.POWER_ID);
        if (power == null || power.amount <= 0) {
            return SpireReturn.Continue();
        }

        if (!AbstractDungeon.getCurrRoom().isBattleOver) {
            __instance.addToBottom(new DrawCardAction((AbstractCreature) null, AbstractDungeon.player.gameHandSize, true));
            AbstractDungeon.player.applyStartOfTurnPostDrawRelics();
            AbstractDungeon.player.applyStartOfTurnPostDrawPowers();
            __instance.addToBottom(new EnableEndTurnButtonAction());
        }

        Yi yi = (Yi) power;
        yi.subAmount();

        return SpireReturn.Return();
    }
}
