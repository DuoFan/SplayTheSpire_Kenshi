package game.duofan.kenshi.patch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import game.duofan.kenshi.KenShi;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class VictoryScreenPatch {

    static VictoryAnim victoryAnim;

    static VictoryAnim getVictoryAnim(){
        if(victoryAnim == null){
            victoryAnim = new VictoryAnim();
        }
        return victoryAnim;
    }

    @SpirePatch(clz = VictoryScreen.class, method = "render")
    public static class VictoryScreenSpineRenderPatch {

        @SpirePrefixPatch
        public static void Prefix(VictoryScreen __instance, SpriteBatch sb) {
            if(AbstractDungeon.player.chosenClass.equals(KenShi.CharacterEnum.CHARACTER_KENSHI)){
                getVictoryAnim().loadAnimation();
                getVictoryAnim().setAnimation();
                getVictoryAnim().render(sb);
            }
        }
    }
}
