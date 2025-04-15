package game.duofan.kenshi.patch;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import game.duofan.kenshi.KenShi;

import java.util.ArrayList;
import java.util.Iterator;

public class CharacterSelectScreenPatches {

    static String KENSHI_NAME = KenShi.characterStrings.NAMES[0];
    static KenshiPortraitAnim kenshiPortraitAnim;

    static KenshiPortraitAnim getKenshiPortraitAnim(){
        if(kenshiPortraitAnim == null){
            kenshiPortraitAnim = new KenshiPortraitAnim();
        }
        return kenshiPortraitAnim;
    }

    @SpirePatch(
            clz = CharacterOption.class,
            method = "updateHitbox"
    )
    public static class CharacterOptionPatch_reloadAnimation {
        public CharacterOptionPatch_reloadAnimation() {
        }

        @SpireInsertPatch(
                rloc = 56
        )
        public static void Insert(CharacterOption __instance) {
            boolean isSelected = __instance.name.equals(KENSHI_NAME);
            if(isSelected){
                getKenshiPortraitAnim().loadAnimation();
                getKenshiPortraitAnim().setAnimation();
                
            }
        }
    }

    @SpirePatch(
            clz = CharacterSelectScreen.class,
            method = "render"
    )
    public static class CharacterSelectScreenPatch_portraitSkeleton {
        public CharacterSelectScreenPatch_portraitSkeleton() {
        }
        @SpireInsertPatch(
                rloc = 62
        )
        public static void Insert(CharacterSelectScreen __instance, SpriteBatch sb) {
            Iterator iterator = __instance.options.iterator();

            while(iterator.hasNext()) {
                CharacterOption o = (CharacterOption)iterator.next();

                if(o.selected && o.name.equals(KENSHI_NAME)){
                    getKenshiPortraitAnim().render(sb);
                }
            }
        }
    }
}
