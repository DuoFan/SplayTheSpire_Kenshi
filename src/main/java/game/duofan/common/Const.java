package game.duofan.common;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import game.duofan.kenshi.KenShi;

public class Const {
    public static final String PACKAGE_NAME = "game.duofan.kenshi";
    public static final Color CHARACTER_RGB_COLOR = new Color(140.0F / 255.0F, 87.0F / 255.0F, 61.0F / 255.0F, 1.0F);
    public static final String CHARACTER_HEX_COLOR = "#8C573D";

    public static final AbstractCard.CardColor KENSHI_CARD_COLOR = KenShi.CharacterEnum.COLOR_KENSHI;

    public static final float PREVIEW_SCALE = 0.75f;
    public static final float PREVIEW_OFFSET_X = 240;
}
