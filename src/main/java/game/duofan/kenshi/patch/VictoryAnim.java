package game.duofan.kenshi.patch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

public class VictoryAnim {
    private String portraitAtlasUrl = "spineAnimation/Victory/jianke_jiesuan.atlas";
    private String portraitJsonUrl = "spineAnimation/Victory/jianke_jiesuan.json";
    public TextureAtlas portraitAtlas = null;
    public Skeleton portraitSkeleton;
    public AnimationState portraitState;
    public AnimationStateData portraitStateData;
    public SkeletonData portraitData;

    boolean loaded;
    boolean setted;

    public void loadAnimation() {
        if(!loaded){
            //portraitAnimation_IMG = new Texture(portraitIMGUrl);
            //portraitAnimation_IMG.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            this.portraitAtlas = new TextureAtlas(Gdx.files.internal(portraitAtlasUrl));
            SkeletonJson json = new SkeletonJson(this.portraitAtlas);
            json.setScale(Settings.scale / 1.0F);
            this.portraitData = json.readSkeletonData(Gdx.files.internal(portraitJsonUrl));
            this.portraitSkeleton = new Skeleton(this.portraitData);
            this.portraitSkeleton.setColor(Color.WHITE);
            this.portraitStateData = new AnimationStateData(this.portraitData);
            this.portraitState = new AnimationState(this.portraitStateData);
            this.portraitStateData.setDefaultMix(0.2F);
            this.portraitState.setTimeScale(1.0F);
            loaded = true;
        }
    }

    public void setAnimation() {
        if(!setted){
            this.portraitState.setAnimation(0, "idle", true);
            setted = true;
        }
    }

    public void render(SpriteBatch sb) {
        this.portraitState.update(Gdx.graphics.getDeltaTime());
        this.portraitState.apply(this.portraitSkeleton);
        this.portraitSkeleton.updateWorldTransform();
        this.portraitSkeleton.setPosition(0 * Settings.scale, 0 * Settings.scale);
        this.portraitSkeleton.setColor(Color.WHITE.cpy());
        this.portraitSkeleton.setFlip(false, false);
        sb.end();
        CardCrawlGame.psb.begin();
        AbstractCreature.sr.draw(CardCrawlGame.psb, this.portraitSkeleton);
        CardCrawlGame.psb.end();
        sb.begin();
    }
}
