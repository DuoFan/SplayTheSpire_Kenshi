package game.duofan.kenshi.effect;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class WeaveEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 1.0F;
    private float x;
    private float y;
    private float vfxTimer;
    private static final float VFX_INTERVAL = 0.016F;

    Color color;

    public WeaveEffect(float newX, float newY, Color _color) {
        this.duration = 1.0F;
        this.x = newX;
        this.y = newY;
        color = _color.cpy();
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.vfxTimer -= Gdx.graphics.getDeltaTime();
        if (this.vfxTimer < 0.0F) {
            this.vfxTimer = 0.016F;
            AbstractDungeon.effectsQueue.add(new InternalEffect(this.x, this.y, color.cpy()));
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }


    class InternalEffect extends AbstractGameEffect {
        private static final float EFFECT_DUR = 2.0F;
        private float x;
        private float y;
        private float speed;
        private float speedStart;
        private float speedTarget;
        private float flipper;
        private TextureAtlas.AtlasRegion img;

        public InternalEffect(float x, float y, Color color) {
            this.img = ImageMaster.BLUR_WAVE;
            this.rotation = MathUtils.random(360.0F);
            this.scale = MathUtils.random(0.2F, 0.4F);
            this.x = x - (float) this.img.packedWidth / 2.0F;
            this.y = y - (float) this.img.packedHeight / 2.0F;
            this.duration = 2.0F;
            this.color = color.cpy();
            this.renderBehind = MathUtils.randomBoolean();
            this.speedStart = MathUtils.random(300.0F, 1000.0F) * Settings.scale;
            this.speedTarget = 900.0F * Settings.scale;
            this.speed = this.speedStart;
            if (MathUtils.randomBoolean()) {
                this.flipper = 90.0F;
            } else {
                this.flipper = 270.0F;
            }

            this.color.g -= MathUtils.random(this.color.g * 0.1F);
            this.color.b -= MathUtils.random(this.color.b * 0.2f);
            this.color.a = 0.0F;
        }

        public void update() {
            Vector2 tmp = new Vector2(MathUtils.cosDeg(this.rotation), MathUtils.sinDeg(this.rotation));
            tmp.x *= this.speed * Gdx.graphics.getDeltaTime();
            tmp.y *= this.speed * Gdx.graphics.getDeltaTime();
            this.speed = Interpolation.fade.apply(this.speedStart, this.speedTarget, 1.0F - this.duration / 2.0F);
            this.x += tmp.x;
            this.y += tmp.y;
            this.scale += Gdx.graphics.getDeltaTime();
            this.duration -= Gdx.graphics.getDeltaTime();
            if (this.duration < 0.0F) {
                this.isDone = true;
            } else if (this.duration > 1.5F) {
                this.color.a = Interpolation.fade.apply(0.0F, 0.7F, (2.0F - this.duration) * 2.0F);
            } else if (this.duration < 0.5F) {
                this.color.a = Interpolation.fade.apply(0.0F, 0.7F, this.duration * 2.0F);
            }

        }

        public void render(SpriteBatch sb) {
            sb.setBlendFunction(770, 1);
            sb.setColor(this.color);
            sb.draw(this.img, this.x, this.y, (float) this.img.packedWidth / 2.0F, (float) this.img.packedHeight / 2.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale + MathUtils.random(-0.08F, 0.08F) * Settings.scale, this.scale + MathUtils.random(-0.08F, 0.08F) * Settings.scale, this.rotation + this.flipper + MathUtils.random(-5.0F, 5.0F));
            sb.setBlendFunction(770, 771);
        }

        public void dispose() {
        }
    }

}
