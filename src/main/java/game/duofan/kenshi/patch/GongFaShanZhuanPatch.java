package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BeatOfDeathPower;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.GongFaShanZhuan;
import game.duofan.kenshi.power.ZhanYanLuo;
import game.duofan.kenshi.relic.YiQi;

@SpirePatch2(clz = DamageAction.class, method = "update")
public class GongFaShanZhuanPatch {

    static DamageAction _lastInstance;

    @SpireInsertPatch(rloc = 80 - 65)
    public static SpireReturn Patch(DamageAction __instance,DamageInfo ___info) {
        if(___info.type == DamageInfo.DamageType.HP_LOSS || __instance == _lastInstance){
            return SpireReturn.Continue();
        }

        AbstractPlayer p = AbstractDungeon.player;

        if(__instance.target != p){
            return SpireReturn.Continue();
        }

        AbstractPower power = p.getPower(GongFaShanZhuan.POWER_ID);
        if(power == null ||  power.amount <= 0){
            return SpireReturn.Continue();
        }

        AbstractPower bodP = __instance.source.getPower(BeatOfDeathPower.POWER_ID);

        if(bodP != null && bodP.amount >= 1 && ___info.output == bodP.amount){
            if(((GongFaShanZhuan)power).isPlayerTurn){
                return SpireReturn.Continue();
            }
        }

        _lastInstance = __instance;

        __instance.isDone = false;
        AbstractDungeon.actionManager.addToTop(__instance);

        power.flash();
        GainBlockAction a = new GainBlockAction(p, power.amount,true);
        AbstractDungeon.actionManager.addToTop(a);
        AbstractDungeon.actionManager.currentAction = a;

        return SpireReturn.Return();
    }
}
