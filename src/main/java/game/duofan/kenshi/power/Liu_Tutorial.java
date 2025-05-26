package game.duofan.kenshi.power;

import basemod.abstracts.CustomMultiPageFtue;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class Liu_Tutorial {

    public void showTutorialPanel(){

        Texture[] textures = new Texture[];
        AbstractDungeon.ftue = new CustomMultiPageFtue(new Texture[]{ImageMaster.loadImage(TimeWalkerMod.getImagePath("ui/tip/t1.png")), ImageMaster.loadImage(TimeWalkerMod.getImagePath("ui/tip/t2.png")), ImageMaster.loadImage(TimeWalkerMod.getImagePath("ui/tip/t2.png"))}, CardCrawlGame.languagePack.getTutorialString(TimeWalkerMod.makeId("TimeWalker")).TEXT);
    }
}
