package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import game.duofan.common.*;
import game.duofan.kenshi.card.FZL_HuiFengZhan;
import game.duofan.kenshi.card.IQiMin;

public class ZhuLiuBaiJia {
    public static boolean canForceInvokeLiu() {
        if (AbstractDungeon.player == null) {
            return false;
        }
        AbstractPower p = AbstractDungeon.player.getPower(XinSuiYiDong.POWER_ID);
        return p != null && ((XinSuiYiDong) p).getTurnAmount() > 0;
    }
}
