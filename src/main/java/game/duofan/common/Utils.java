package game.duofan.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.kenshi.action.DrawCardByClass;
import game.duofan.kenshi.power.AnYing;
import game.duofan.kenshi.power.IFengZhiLiuCard;
import game.duofan.kenshi.power.Qi;

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

    public static void playerGainPower(AbstractPower power) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, power)
        );
    }

    public static void playReducePower(String powerID, int amount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            AbstractDungeon.actionManager.addToBottom(
                    new ReducePowerAction(p, p, powerID, amount));
        }
    }

    public static void playRemovePower(String powerID) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            AbstractDungeon.actionManager.addToBottom(new
                    RemoveSpecificPowerAction(p, p, powerID));
        }
    }

    public static void playerEnterAnYin() {
        playerGainPower(new AnYing(AbstractDungeon.player));
    }

    public static void playerGainStrength(int amount) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                        new StrengthPower(AbstractDungeon.player, amount))
        );
    }

    public static void playerGainEnergy(int amount) {
        AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(amount));
    }

    public static void playerGainBlock(int amount) {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, amount));
    }

    public static void playerGainQi(int amount) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new Qi(AbstractDungeon.player, amount))
        );
    }

    public static void playerReduceQi(int amount) {
        playReducePower(Qi.POWER_ID, amount);
    }

    public static int getQiAmount() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null || !p.hasPower(Qi.POWER_ID)) {
            return 0;
        }

        return p.getPower(Qi.POWER_ID).amount;
    }

    public static void playerDrawCardByClass(int amount, Class<?> targetClass) {
        AbstractDungeon.actionManager.addToBottom(new DrawCardByClass(amount, targetClass));
    }
}
