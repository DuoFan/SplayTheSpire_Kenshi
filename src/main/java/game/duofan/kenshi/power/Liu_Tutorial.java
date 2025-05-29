package game.duofan.kenshi.power;

import basemod.abstracts.CustomMultiPageFtue;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import game.duofan.common.Utils;

import java.io.IOException;

public class Liu_Tutorial {

    public void tryShowTutorial1() {
        SpireConfig config = null;
        try {
            config = Utils.getCommonConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String tutorialID = Utils.generateID("tutorial1");
        if (config.has(tutorialID)) {
            return;
        }

        config.setBool(tutorialID, true);
        try {
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String lang;
        if (Settings.language == Settings.GameLanguage.ZHS) {
            lang = "ZHS";
        } else {
            lang = "ENG";
        }

        AbstractDungeon.ftue = new CustomMultiPageFtue(new Texture[]{
                ImageMaster.loadImage("img/tutorial/" + lang + "/t1_0.png"),
                ImageMaster.loadImage("img/tutorial/" + lang + "/t1_1.png"),
        },
                CardCrawlGame.languagePack.getTutorialString(tutorialID).TEXT
        );
    }

    public void showTutorial2() {
        SpireConfig config = null;
        try {
            config = Utils.getCommonConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String tutorialID = Utils.generateID("tutorial2");
        if (config.has(tutorialID)) {
            return;
        }

        config.setBool(tutorialID, true);
        try {
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String lang;
        if (Settings.language == Settings.GameLanguage.ZHS) {
            lang = "ZHS";
        } else {
            lang = "ENG";
        }

        AbstractDungeon.ftue = new CustomMultiPageFtue(new Texture[]{
                ImageMaster.loadImage("img/tutorial/" + lang + "/t2_0.png"),
                ImageMaster.loadImage("img/tutorial/" + lang + "/t2_1.png"),
                ImageMaster.loadImage("img/tutorial/" + lang + "/t2_2.png"),
        },
                CardCrawlGame.languagePack.getTutorialString(tutorialID).TEXT
        );
    }
}
