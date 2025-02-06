package game.duofan.common;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.MiracleEffect;

public class Utils {
    public static String generateID(String id) {
        return Const.PACKAGE_NAME + ":" + id;
    }

    public static void addToBotAbstract(Lambda func) {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                func.run();
                isDone = true;
            }
        });
    }

    public static void addToTopAbstract(Lambda func) {
        AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                func.run();
                isDone = true;
            }
        });
    }

    public interface Lambda extends Runnable {
    }

    public static boolean isLastUsedCardType(AbstractCard.CardType cardType) {
        AbstractCard card = getLastUsedCard();
        if (card == null) {
            return false;
        }

        return card.type == cardType;
    }

    public static boolean isLastXUsedCardType(int x, AbstractCard.CardType cardType) {
        AbstractCard card = getLastXUsedCard(x);
        if (card == null) {
            return false;
        }

        return card.type == cardType;
    }

    public static AbstractCard getLastUsedCard() {
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty()) {
            return null;
        }
        return AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1);
    }

    public static AbstractCard getLastXUsedCard(int x) {
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() < x) {
            return null;
        }

        return AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - x);
    }
}
