package game.duofan.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.options.ExitGameButton;

public class DF_Sprite {
    public RectTransform rect;
    public Texture texture;
    public Color color = Color.WHITE.cpy();
    public boolean useTextureSize;

    public DF_Sprite(String imgUrl, RectTransform _rect) {
        texture = ImageMaster.loadImage(imgUrl);
        rect = _rect;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(color);
        float width = useTextureSize ? texture.getWidth() : rect.width;
        float height = useTextureSize ? texture.getHeight() : rect.height;
        sb.draw(texture, rect.posX - width / 2.0F, rect.posY - height / 2.0F, width / 2.0F,
                height / 2.0F, width, height, rect.scaleX, rect.scaleY,
                0.0F, 0, 0, (int) width, (int) height, false, false);
    }
}
