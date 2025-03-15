package game.duofan.kenshi.patch;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
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

    /*@SpirePatch(
            clz = CharacterSelectScreen.class,
            method = "update"
    )
    public static class CharacterSelectScreenPatch_Update {
        public CharacterSelectScreenPatch_Update() {
        }

        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance) {
            Iterator var1 = __instance.options.iterator();

            while(var1.hasNext()) {
                CharacterOption o = (CharacterOption)var1.next();
                AbstractSkinCharacter[] var3 = CharacterSelectScreenPatches.characters;
                int var4 = var3.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    AbstractSkinCharacter c = var3[var5];
                    c.InitializeReskinCount();
                    if (o.name.equals(c.id) && o.selected && c.reskinUnlock) {
                        if (!CharacterSelectScreenPatches.bgIMGUpdate) {
                            __instance.bgCharImg = c.skins[c.reskinCount].updateBgImg();
                            CharacterSelectScreenPatches.bgIMGUpdate = true;
                        }

                        if (InputHelper.justClickedLeft && CharacterSelectScreenPatches.reskinLeft.hovered) {
                            CharacterSelectScreenPatches.reskinLeft.clickStarted = true;
                            CardCrawlGame.sound.play("UI_CLICK_1");
                        }

                        if (InputHelper.justClickedLeft && CharacterSelectScreenPatches.reskinRight.hovered) {
                            CharacterSelectScreenPatches.reskinRight.clickStarted = true;
                            CardCrawlGame.sound.play("UI_CLICK_1");
                        }

                        if (InputHelper.justClickedLeft && CharacterSelectScreenPatches.portraitAnimationLeft.hovered && c.reskinCount > 0) {
                            CharacterSelectScreenPatches.portraitAnimationLeft.clickStarted = true;
                            CardCrawlGame.sound.play("UI_CLICK_1");
                        }

                        if (InputHelper.justClickedLeft && CharacterSelectScreenPatches.portraitAnimationRight.hovered && c.reskinCount > 0) {
                            CharacterSelectScreenPatches.portraitAnimationRight.clickStarted = true;
                            CardCrawlGame.sound.play("UI_CLICK_1");
                        }

                        if (CharacterSelectScreenPatches.reskinLeft.justHovered || CharacterSelectScreenPatches.reskinRight.justHovered) {
                            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
                        }

                        if ((CharacterSelectScreenPatches.portraitAnimationLeft.justHovered || CharacterSelectScreenPatches.portraitAnimationRight.justHovered) && c.reskinCount > 0) {
                            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
                        }

                        CharacterSelectScreenPatches.reskinRight.move((float)Settings.WIDTH / 2.0F + CharacterSelectScreenPatches.reskin_W / 2.0F - CharacterSelectScreenPatches.reskinX_center + CharacterSelectScreenPatches.allTextInfoX, CharacterSelectScreenPatches.allTextInfoY + 0.0F * Settings.scale);
                        CharacterSelectScreenPatches.reskinLeft.move((float)Settings.WIDTH / 2.0F - CharacterSelectScreenPatches.reskin_W / 2.0F - CharacterSelectScreenPatches.reskinX_center + CharacterSelectScreenPatches.allTextInfoX, CharacterSelectScreenPatches.allTextInfoY + 0.0F * Settings.scale);
                        CharacterSelectScreenPatches.portraitAnimationLeft.move((float)Settings.WIDTH / 2.0F - CharacterSelectScreenPatches.reskin_W / 2.0F - CharacterSelectScreenPatches.reskinX_center + CharacterSelectScreenPatches.allTextInfoX, CharacterSelectScreenPatches.allTextInfoY + 120.0F * Settings.scale);
                        CharacterSelectScreenPatches.portraitAnimationRight.move((float)Settings.WIDTH / 2.0F + CharacterSelectScreenPatches.reskin_W / 2.0F - CharacterSelectScreenPatches.reskinX_center + CharacterSelectScreenPatches.allTextInfoX, CharacterSelectScreenPatches.allTextInfoY + 120.0F * Settings.scale);
                        CharacterSelectScreenPatches.reskinLeft.update();
                        CharacterSelectScreenPatches.reskinRight.update();
                        if (c.reskinCount > 0) {
                            CharacterSelectScreenPatches.portraitAnimationLeft.update();
                            CharacterSelectScreenPatches.portraitAnimationRight.update();
                        }

                        if (CharacterSelectScreenPatches.reskinRight.clicked || CInputActionSet.pageRightViewExhaust.isJustPressed()) {
                            CharacterSelectScreenPatches.reskinRight.clicked = false;
                            c.skins[c.reskinCount].clearWhenClick();
                            CharacterSelectScreenPatches.char_effectsQueue.clear();
                            if (c.reskinCount < c.skins.length - 1) {
                                ++c.reskinCount;
                            } else {
                                c.reskinCount = 0;
                            }

                            c.skins[c.reskinCount].loadPortraitAnimation();
                            __instance.bgCharImg = c.skins[c.reskinCount].updateBgImg();
                        }

                        if (CharacterSelectScreenPatches.reskinLeft.clicked || CInputActionSet.pageRightViewExhaust.isJustPressed()) {
                            CharacterSelectScreenPatches.reskinLeft.clicked = false;
                            c.skins[c.reskinCount].clearWhenClick();
                            CharacterSelectScreenPatches.char_effectsQueue.clear();
                            if (c.reskinCount > 0) {
                                --c.reskinCount;
                            } else {
                                c.reskinCount = c.skins.length - 1;
                            }

                            c.skins[c.reskinCount].loadPortraitAnimation();
                            __instance.bgCharImg = c.skins[c.reskinCount].updateBgImg();
                        }

                        if (CharacterSelectScreenPatches.portraitAnimationLeft.clicked || CInputActionSet.pageRightViewExhaust.isJustPressed()) {
                            CharacterSelectScreenPatches.portraitAnimationLeft.clicked = false;
                            c.skins[c.reskinCount].clearWhenClick();
                            CharacterSelectScreenPatches.char_effectsQueue.clear();
                            if (c.skins[c.reskinCount].portraitAnimationType <= 0) {
                                c.skins[c.reskinCount].portraitAnimationType = 2;
                            } else {
                                --c.skins[c.reskinCount].portraitAnimationType;
                            }

                            c.skins[c.reskinCount].loadPortraitAnimation();
                            __instance.bgCharImg = c.skins[c.reskinCount].updateBgImg();
                        }

                        if (CharacterSelectScreenPatches.portraitAnimationRight.clicked || CInputActionSet.pageRightViewExhaust.isJustPressed()) {
                            CharacterSelectScreenPatches.portraitAnimationRight.clicked = false;
                            c.skins[c.reskinCount].clearWhenClick();
                            CharacterSelectScreenPatches.char_effectsQueue.clear();
                            if (c.skins[c.reskinCount].portraitAnimationType >= 2) {
                                c.skins[c.reskinCount].portraitAnimationType = 0;
                            } else {
                                ++c.skins[c.reskinCount].portraitAnimationType;
                            }

                            c.skins[c.reskinCount].loadPortraitAnimation();
                            __instance.bgCharImg = c.skins[c.reskinCount].updateBgImg();
                        }

                        c.skins[c.reskinCount].update();
                        if (c.skins[c.reskinCount].extraHitboxClickCheck()) {
                            __instance.bgCharImg = c.skins[c.reskinCount].updateBgImg();
                        }

                        c.InitializeReskinCount();
                    }
                }
            }

        }
    }*/

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
                    System.out.println("--------");
                    getKenshiPortraitAnim().render(sb);
                }
            }
        }
    }
}
