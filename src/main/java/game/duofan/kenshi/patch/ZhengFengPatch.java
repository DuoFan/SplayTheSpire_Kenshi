package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.ZhengFeng;

import java.lang.reflect.Field;

@SpirePatch2(clz = AbstractPlayer.class, method = "playCard")
public class ZhengFengPatch {
    @SpireInsertPatch(rloc = 0)
    public static SpireReturn Insert(AbstractPlayer __instance) throws NoSuchFieldException, IllegalAccessException {

        System.out.println("----------Insert");

        boolean check = zhengFengCheck(__instance, __instance.hoveredCard);

        System.out.println("----------Insert" + check);

        if (check) {
            return SpireReturn.Continue();
        } else {
            handleFailure(__instance);
            return SpireReturn.Return();
        }
    }

    static boolean zhengFengCheck(AbstractPlayer p, AbstractCard c) {
        // 检查手牌中是否有未使用的"争锋"卡
        boolean hasUnplayedZhengFeng = false;

        for (int i = 0; i < p.hand.size(); i++) {
            AbstractCard _c = p.hand.group.get(i);
            if(_c instanceof ZhengFeng){
                hasUnplayedZhengFeng = true;
                break;
            }
        }

        if (hasUnplayedZhengFeng) {
            // 1. 当前卡是"争锋"
            // 2. 玩家正在结束回合
            // 3. 卡牌被强制使用（如遗物效果）
            if (c instanceof ZhengFeng ||
                    p.isEndingTurn ||
                    c.isInAutoplay
            ) {
                return true;
            }
            return false;
        }

        return true;
    }

    static void handleFailure(AbstractPlayer p) throws IllegalAccessException, NoSuchFieldException {
        InputHelper.justClickedLeft = false;

        p.inSingleTargetMode = false;
        p.releaseCard();
        p.hoverEnemyWaitTimer = 1.0F;
        p.hoveredCard = null;
        p.isDraggingCard = false;

        GameCursor.hidden = false;

        Field f = AbstractPlayer.class.getDeclaredField("hoveredMonster");
        f.setAccessible(true);
        f.set(p, null);
        f.setAccessible(false);

        f = AbstractPlayer.class.getDeclaredField("isUsingClickDragControl");
        f.setAccessible(true);
        f.set(p, false);
        f.setAccessible(false);

        UIStrings string = CardCrawlGame.languagePack.getUIString("game.duofan.kenshi:ZhengFengFirst");
        Utils.showToast(string.TEXT[0]);
    }
}