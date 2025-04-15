package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.ending.SpireSpear;
import game.duofan.common.AudioKey;
import game.duofan.kenshi.KenShi;

@SpirePatch2(clz = SpireSpear.class, method = "usePreBattleAction")
public class Theme_ChallengerPatch {
    @SpireInsertPatch(rloc = 77 - 71)
    public static void Patch() {
        if(AbstractDungeon.player != null && AbstractDungeon.player instanceof KenShi){
            AbstractDungeon.getCurrRoom().playBgmInstantly(AudioKey.THEME_CHALLENGER);
        }
    }
}

