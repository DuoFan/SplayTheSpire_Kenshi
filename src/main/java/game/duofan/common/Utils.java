package game.duofan.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import com.megacrit.cardcrawl.vfx.combat.BlockedNumberEffect;
import game.duofan.kenshi.action.*;
import game.duofan.kenshi.card.*;
import game.duofan.kenshi.power.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static String generateID(String id) {
        return Const.PACKAGE_NAME + ":" + id;
    }

    static SpireConfig commonConfig;

    public static SpireConfig getCommonConfig() throws IOException {
        if (commonConfig == null) {
            commonConfig = new SpireConfig("JianKe", "commonConfig");
        }
        return commonConfig;
    }

    public static void showToast(String s) {
        AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, s, true));
    }

    public static void insertAbstract(Lambda func, int index) {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                func.run();
                isDone = true;
            }
        });
        int tail = AbstractDungeon.actionManager.actions.size() - 1;
        AbstractGameAction a = AbstractDungeon.actionManager.actions.get(tail);
        AbstractDungeon.actionManager.actions.remove(tail);
        AbstractDungeon.actionManager.actions.add(index, a);
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

    public static void pickUpCardsDoAction(String _text, int amount, IDoCard _action) {
        AbstractDungeon.actionManager.addToBottom(new PickUpCardsDoAction(_text, amount, _action));
    }

    public static boolean isIntentAttack(AbstractMonster m) {
        return m.intent == AbstractMonster.Intent.ATTACK ||
                m.intent == AbstractMonster.Intent.ATTACK_BUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }

    public static boolean isKilledUnMinion(AbstractCreature m) {
        if (m.hasPower("Minion")) {
            return false;
        }
        return isKilled(m);
    }

    public static boolean isKilled(AbstractCreature m) {
        return (m.isDying || m.currentHealth <= 0) && !m.halfDead;
    }

    public static void upgradeCardContainer(ICardContainer container) {
        if (container == null) {
            return;
        }

        CardGroup g = container.getContainer();
        if (g == null) {
            return;
        }

        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.getNCardFromTop(i);
            if (c.canUpgrade()) {
                c.upgrade();
            }
        }
    }

    public static void exhaustCardContainer(ICardContainer container) {
        if (container == null) {
            return;
        }

        CardGroup g = container.getContainer();
        if (g == null) {
            return;
        }

        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.getNCardFromTop(i);
            if (c != null) {
                AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(c, g));
            }
        }
    }

    public static void updateCardContainer(ICardContainer container) {
        if (container == null) {
            return;
        }

        CardGroup g = container.getContainer();
        if (g != null) {
            for (int i = 0; i < g.size(); i++) {
                AbstractCard childCard = g.getNCardFromTop(i);
                childCard.update();
            }
        }

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return;
        }

        if (p.isDraggingCard && p.hoveredCard.equals(container)) {
            updateCardPosition((AbstractCard) container);
        }
    }

    private static void updateCardPosition(AbstractCard c) {
        CardGroup hand = AbstractDungeon.player.hand;
        int oriIndex = hand.group.indexOf(c);
        int newIndex = calculateNewCardIndex();
        if (newIndex != oriIndex) {
            // 移动卡牌到新位置
            addToBotAbstract(() ->
            {
                hand.group.remove(c);
                hand.group.add(newIndex, c);
                hand.refreshHandLayout();
            });
        }
    }

    private static int calculateNewCardIndex() {
        CardGroup hand = AbstractDungeon.player.hand;

        float mouseX = Gdx.input.getX();
        int closestIndex = 0;
        float minDistance = Float.MAX_VALUE;

        // 计算距离鼠标最近的卡牌位置
        for (int i = 0; i < hand.group.size(); i++) {
            AbstractCard c = hand.group.get(i);
            float distance = Math.abs(c.current_x - mouseX);
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    public static void renderCardContainer(SpriteBatch sb, ICardContainer container) {
        if (container == null) {
            return;
        }

        CardGroup g = container.getContainer();
        if (g == null || g.isEmpty()) {
            return;
        }

        AbstractCard parent = (AbstractCard) container;

        // 获取父卡牌的屏幕位置
        float parentX = parent.current_x;
        float parentY = parent.current_y;

        float[] CARD_OFFSETS_X = container.getCardOffsetsX();
        float[] CARD_OFFSETS_Y = container.getCardOffsetsY();
        float CARD_SCALE = container.getCardScale();

        // 遍历所有子卡牌
        for (int i = 0; i < g.size(); i++) {
            AbstractCard childCard = g.getNCardFromTop(i);

            // 设置子卡牌位置（相对父卡偏移）
            childCard.target_x = parentX + CARD_OFFSETS_X[i % CARD_OFFSETS_X.length] * parent.drawScale;
            childCard.target_y = parentY + CARD_OFFSETS_Y[i % CARD_OFFSETS_Y.length] * parent.drawScale;

            // 固定缩放比例
            childCard.targetDrawScale = parent.drawScale * CARD_SCALE;
            childCard.setAngle(parent.angle);

            // 禁用交互区域
            childCard.hb.move(0, 0); // 隐藏点击区域

            // 渲染子卡牌（需要复制原渲染逻辑）
            childCard.render(sb);
        }
    }

    public static void glowCheckCardContainer(ICardContainer container) {
        if (container == null) {
            return;
        }

        CardGroup g = container.getContainer();
        if (g == null || g.isEmpty()) {
            return;
        }

        for (int i = 0; i < g.size(); i++) {
            AbstractCard childCard = g.getNCardFromTop(i);
            childCard.triggerOnGlowCheck();
        }
    }

    public static void returnLastLiuAction() {
        Liu_StateMachine.StateEnum liu = Liu_StateMachine.getInstance().lastLiu();
        if (liu != Liu_StateMachine.StateEnum.None) {
            Utils.addToBotAbstract(() -> {
                Liu_StateMachine.getInstance().changeLiu(liu);
            });
        }
    }

    public interface Lambda extends Runnable {
    }

    public static void liuPowerOnUseCard(AbstractCard card) {
        Liu_StateMachine.StateEnum liu = Utils.getLiuFromCard(card);

        if (canInvokeLiuEffect(card)) {

            Liu_StateMachine.StateEnum curLiu = Liu_StateMachine.getInstance().getLiu();

            AbstractPower _xinSuiYiDong = AbstractDungeon.player.getPower(XinSuiYiDong.POWER_ID);
            XinSuiYiDong xinSuiYiDong = null;
            if (_xinSuiYiDong != null) {
                xinSuiYiDong = (XinSuiYiDong) _xinSuiYiDong;
            }

            AbstractPower _baiHuaQiFang = AbstractDungeon.player.getPower(BaiHuaQiFang.POWER_ID);
            BaiHuaQiFang baiHuaQiFang = null;
            if (_baiHuaQiFang != null) {
                baiHuaQiFang = (BaiHuaQiFang) _baiHuaQiFang;
            }

            if (card instanceof IXiaZhiLiuCard) {
                Utils.invokeXZL_Effect((IXiaZhiLiuCard) card, false);
            } else {
                Utils.invokeLiuCardEffectWithTiming(card);
            }
            Liu_StateMachine.getInstance().setLastEffectLiuCardOnTurn(card);
            Liu_StateMachine.getInstance().setLastEffectLiuCardOnBattle(card);

            if (liu == Liu_StateMachine.StateEnum.FengZhiLiu && baiHuaQiFang != null) {
                baiHuaQiFang.flash();
            } else if (xinSuiYiDong != null && xinSuiYiDong.getTurnAmount() > 0) {
                xinSuiYiDong.subTurnAmountToEffect();
            }

            Liu_StateMachine.getInstance().setDriving(curLiu, liu);
            Liu_StateMachine.getInstance().changeLiu(liu);

            if (Utils.getQiAmount() > 0 && !InvalidLiuCardManager.getInstance().isTagCard(card)) {
                AbstractPower qi = AbstractDungeon.player.getPower(Qi.POWER_ID);
                if (qi != null) {
                    qi.flash();
                }
                if (card instanceof IXiaZhiLiuCard) {
                    Utils.invokeXZL_Effect((IXiaZhiLiuCard) card, true);
                } else {
                    Utils.invokeLiuCardEffectWithTiming(card);
                }
                if (card instanceof IQiMin) {
                    Utils.invokeLiuCardEffectWithTiming(card);
                }
                Utils.playerReduceQi(1);
            }
        } else if (liu != Liu_StateMachine.StateEnum.None) {
            Liu_StateMachine.getInstance().changeLiu(liu);
        }
    }

    public static void liuPowerAtEndOfTurn() {
        Liu_StateMachine.getInstance().reset();
        Liu_StateMachine.getInstance().clearFlags();
        Liu_StateMachine.getInstance().clearLastEffectLiuCardOnTurn();
        Liu_StateMachine.getInstance().clearEnterLiuAmountOnTurn();
    }

    public static boolean canInvokeLiuEffect(AbstractCard c) {
        if (AbstractDungeon.player == null) {
            return false;
        }

        Liu_StateMachine.StateEnum liu = Utils.getLiuFromCard(c);
        if (liu == Liu_StateMachine.StateEnum.None) {
            return false;
        }

        Liu_StateMachine.StateEnum curLiu = Liu_StateMachine.getInstance().getLiu();
        ArrayList<Liu_StateMachine.StateEnum> invokeable = Liu_StateMachine.getInstance().getInvokeable(curLiu);
        return invokeable.indexOf(liu) >= 0;
    }

    public static void invokeLiuCardEffectWithTiming(AbstractCard card) {
        if (card == null) {
            return;
        }

        boolean isInvokeToTop = ((ILiuCard) card).isInvokeLiuEffectToTop();
        if (isInvokeToTop) {
            Utils.addToTopAbstract(() -> {
                invokeLiuCardEffect(card);
            });
        } else {
            Utils.addToBotAbstract(() -> {
                invokeLiuCardEffect(card);
            });
        }
    }

    public static void invokeLiuCardEffect(AbstractCard card) {
        Liu_StateMachine.StateEnum stateEnum = getLiuFromCard(card);
        if (stateEnum == Liu_StateMachine.StateEnum.None) {
            return;
        }

        switch (stateEnum) {
            case FengZhiLiu:
                ((IFengZhiLiuCard) card).fengZhiLiuEffect();
                break;
            case XiaZhiLiu:
                ((IXiaZhiLiuCard) card).xiaZhiLiuEffect(false);
                break;
            case YuZhiLiu:
                ((IYuZhiLiuCard) card).yuZhiLiuEffect();
                break;
            case YanZhiLiu:
                ((IYanZhiLiuCard) card).yanZhiLiuEffect();
                break;
        }
    }

    public static void invokeXZL_Effect(IXiaZhiLiuCard card, boolean isByQi) {
        if (card == null) {
            return;
        }

        boolean isInvokeToTop = card.isInvokeLiuEffectToTop();
        if (isInvokeToTop) {
            Utils.addToTopAbstract(() -> {
                card.xiaZhiLiuEffect(isByQi);
            });
        } else {
            Utils.addToBotAbstract(() -> {
                card.xiaZhiLiuEffect(isByQi);
            });
        }
    }

    public static void makeTempCardInDrawPileAction(AbstractCard card, int amount, boolean randomSpot, boolean autoPosition) {
        if (card == null) {
            return;
        }

        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(card, amount, randomSpot, autoPosition));
    }

    public static void makeTempCardInHand(AbstractCard card, int i) {
        if (card == null) {
            return;
        }

        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, i));
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

    public static void giveDamage(AbstractCreature s, AbstractCreature t, int amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect attackEffect) {
        if (s != null && t != null) {
            AbstractDungeon.actionManager.addToBottom(new
                    DamageAction(t, new DamageInfo(s, amount, type), attackEffect));
        }
    }

    public static void giveDamageTop(AbstractCreature s, AbstractCreature t, int amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect attackEffect) {
        if (s != null && t != null) {
            AbstractDungeon.actionManager.addToTop(new
                    DamageAction(t, new DamageInfo(s, amount, type), attackEffect));
        }
    }

    public static void giveDamageFast(AbstractCreature s, AbstractCreature t, int amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect attackEffect) {
        if (s != null && t != null) {
            AbstractDungeon.actionManager.addToBottom(new
                    DamageAction(t, new DamageInfo(s, amount, type), attackEffect, true));
        }
    }

    public static DamageInfo giveBaoYanDamage(AbstractCreature s, AbstractCreature t, int amount, DamageInfo.DamageType type) {
        if (t != null) {
            DamageInfo info = new DamageInfo(s, amount, type);
            AbstractDungeon.actionManager.addToBottom(new DamageAction(t, info, AbstractGameAction.AttackEffect.FIRE));
            AbstractDungeon.actionManager.addToBottom(new NotifyBaoYanDamageAction(t, info));
            return info;
        } else {
            return null;
        }
    }

    public static void giveBaoYanDamageInTop(AbstractCreature s, AbstractCreature t, int amount, DamageInfo.DamageType type) {
        if (s != null && t != null) {
            DamageInfo info = new DamageInfo(s, amount, type);
            AbstractDungeon.actionManager.addToTop(new NotifyBaoYanDamageAction(t, info));
            AbstractDungeon.actionManager.addToTop(new DamageAction(t, info));
        }
    }

    public static void givePower(AbstractCreature s, AbstractCreature t, AbstractPower power) {
        if (s != null && t != null) {
            AbstractDungeon.actionManager.addToBottom(new
                    ApplyPowerAction(t, s, power));
        }
    }

    public static void givePowerTop(AbstractCreature s, AbstractCreature t, AbstractPower power) {
        if (s != null && t != null) {
            AbstractDungeon.actionManager.addToTop(new
                    ApplyPowerAction(t, s, power));
        }
    }

    public static void gainPower(AbstractCreature o, AbstractPower power) {
        givePower(o, o, power);
    }

    public static void gainPowerTop(AbstractCreature o, AbstractPower power) {
        givePowerTop(o, o, power);
    }

    public static void removePower(AbstractCreature o, String powerID) {
        if (o != null) {
            AbstractDungeon.actionManager.addToBottom(new
                    RemoveSpecificPowerAction(o, o, powerID));
        }
    }

    public static void removePowerTop(AbstractCreature o, String powerID) {
        if (o != null) {
            AbstractDungeon.actionManager.addToTop(new
                    RemoveSpecificPowerAction(o, o, powerID));
        }
    }

    public static void gainBlock(AbstractCreature o, int amount) {
        if (o == null) {
            return;
        }
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(o, amount));
    }

    public static GainBlockAction gainBlockTop(AbstractCreature o, int amount) {
        if (o == null) {
            return null;
        }
        GainBlockAction a = new GainBlockAction(o, amount);
        AbstractDungeon.actionManager.addToTop(a);
        return a;
    }

    public static void gainHeal(AbstractCreature o, int amount) {
        if (o == null) {
            return;
        }
        AbstractDungeon.actionManager.addToBottom(new HealAction(o, o, amount));
    }

    public static void playerGainPower(AbstractPower power) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, power)
        );
    }

    public static void playerGainPowerTop(AbstractPower power) {
        AbstractDungeon.actionManager.addToTop(
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

    public static void playReducePowerTop(String powerID, int amount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            AbstractDungeon.actionManager.addToTop(
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

    public static void playRemovePowerTop(String powerID) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            AbstractDungeon.actionManager.addToTop(new
                    RemoveSpecificPowerAction(p, p, powerID));
        }
    }

    public static void playerEnterAnYin() {
        playerGainPower(new YeBuPower(AbstractDungeon.player));
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
        gainBlock(p, amount);
    }

    public static GainBlockAction playerGainBlockTop(int amount) {
        AbstractPlayer p = AbstractDungeon.player;
        return gainBlockTop(p, amount);
    }

    public static void playerGainQi(int amount) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new Qi(AbstractDungeon.player, amount))
        );
    }

    public static void playerGainQiTop(int amount) {
        AbstractDungeon.actionManager.addToTop(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new Qi(AbstractDungeon.player, amount))
        );
    }

    public static void playerReduceQi(int amount) {
        AbstractPower p = AbstractDungeon.player.getPower(Qi.POWER_ID);
        if (p != null && p.amount > 0) {
            playReducePower(Qi.POWER_ID, amount);
        } else {
            Utils.addToBotAbstract(() -> {
                reduceQiFromQiHai(amount);
            });
        }
    }

    public static void playerReduceQiTop(int amount) {
        AbstractPower p = AbstractDungeon.player.getPower(Qi.POWER_ID);
        if (p != null && p.amount > 0) {
            playReducePowerTop(Qi.POWER_ID, amount);
        } else {
            Utils.addToTopAbstract(() -> {
                reduceQiFromQiHai(amount);
            });
        }
    }

    static void reduceQiFromQiHai(int amount) {
        CardGroup hand = AbstractDungeon.player.hand;
        if (hand == null) {
            return;
        }
        for (int i = 0; i < hand.group.size() && amount > 0; i++) {
            AbstractCard c = hand.group.get(i);
            int reduce = 0;
            if (c instanceof QiHai && c.cost > 0) {
                while (c.cost > 0 && amount > 0) {
                    c.modifyCostForCombat(-1);
                    amount--;
                    reduce++;
                }
                if (reduce > 0) {
                    c.superFlash();
                    AbstractDungeon.effectList.add(new BlockedNumberEffect(c.current_x + -132.0F * c.drawScale * Settings.scale, c.current_y + 220.0F * c.drawScale * Settings.scale, Integer.toString(reduce)));
                }
            }
        }
    }

    public static int getQiAmount() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return 0;
        }

        int qi = 0;

        AbstractPower power = p.getPower(Qi.POWER_ID);

        if (power != null && power.amount > 0) {
            qi = power.amount;
        }

        if (p == null) {
            return qi;
        }

        CardGroup hand = p.hand;
        if (hand == null) {
            return qi;
        }

        for (int i = 0; i < hand.group.size(); i++) {
            AbstractCard c = hand.group.get(i);
            if (c instanceof QiHai && c.cost > 0) {
                qi += c.cost;
            }
        }

        return qi;
    }

    public static void playerGainYi(int amount) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FuYao(AbstractDungeon.player, amount))
        );
    }

    public static void playerGainYiTop(int amount) {
        AbstractDungeon.actionManager.addToTop(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FuYao(AbstractDungeon.player, amount))
        );
    }

    public static void playerReduceYi(int amount) {
        AbstractPower p = AbstractDungeon.player.getPower(FuYao.POWER_ID);
        if (p != null && p.amount > 0) {
            playReducePower(FuYao.POWER_ID, amount);
        }
    }

    public static void playerReduceYiTop(int amount) {
        AbstractPower p = AbstractDungeon.player.getPower(FuYao.POWER_ID);
        if (p != null && p.amount > 0) {
            playReducePowerTop(FuYao.POWER_ID, amount);
        }
    }

    public static boolean needRefreshDiscardPileForDraw(ICardFilter cardFilter, int amount) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return false;
        }

        if (p.hasPower(FanShi.POWER_ID)) {
            return false;
        }

        CardGroup g = p.drawPile;
        if (g == null) {
            return false;
        }

        for (int i = 0; i < g.size() && amount > 0; i++) {
            AbstractCard c = g.getNCardFromTop(i);
            if (cardFilter == null || cardFilter.filter(c)) {
                amount--;
            }
        }

        if (amount <= 0) {
            return false;
        }

        g = p.discardPile;
        if (g == null) {
            return false;
        }

        for (int i = 0; i < g.size() && amount > 0; i++) {
            AbstractCard c = g.getNCardFromTop(i);
            if (cardFilter == null || cardFilter.filter(c)) {
                amount--;
            }
        }

        return amount <= 0;
    }

    public static int calculateRefreshDiscardPileForDraw(ICardFilter cardFilter) {
        int amount = 0;

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) {
            return amount;
        }

        CardGroup g = p.drawPile;

        if (p.hasPower(FanShi.POWER_ID)) {
            g = p.discardPile;
        }
        if (g == null) {
            return amount;
        }

        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.getNCardFromTop(i);
            if (cardFilter == null || cardFilter.filter(c)) {
                amount++;
            }
        }

        if (p.hasPower(FanShi.POWER_ID)) {
            return amount;
        }

        g = p.discardPile;
        if (g == null) {
            return amount;
        }

        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.getNCardFromTop(i);
            if (cardFilter == null || cardFilter.filter(c)) {
                amount++;
            }
        }

        return amount;
    }

    public static DrawCardByFilterAction playerDrawCardByFilterAction(int amount, ICardFilter filter) {
        DrawCardByFilterAction a = new DrawCardByFilterAction(amount, filter);
        AbstractDungeon.actionManager.addToBottom(a);
        return a;
    }

    public static DrawCardByFilterAction playerDrawCardByFilterActionTop(int amount, ICardFilter filter) {
        DrawCardByFilterAction a = new DrawCardByFilterAction(amount, filter);
        AbstractDungeon.actionManager.addToTop(a);
        return a;
    }

    public static void playerDrawCardByClass(int amount, Class<?> targetClass) {
        AbstractDungeon.actionManager.addToBottom(new DrawCardByClassAction(amount, targetClass));
    }

    public static ArrayList<AbstractCard> getCardsFromHand(int baseCost) {

        ArrayList<AbstractCard> cards = new ArrayList<>();

        AbstractPlayer p = AbstractDungeon.player;

        Iterator var3 = p.hand.group.iterator();

        while (var3.hasNext()) {
            AbstractCard c = (AbstractCard) var3.next();
            if (c.costForTurn >= baseCost || c.cost >= baseCost) {
                cards.add(c);
            }
        }

        return cards;
    }

    public static AbstractCard getRandomCardFromHand(int baseCost, AbstractCard excludeCard) {

        ArrayList<AbstractCard> cards = getCardsFromHand(baseCost);

        if (excludeCard != null) {
            cards.remove(excludeCard);
        }

        return getRandomCardsFromList(cards, false);
    }

    public static ArrayList<AbstractCard> getCardsFromLiu(int flag) {
        Liu_StateMachine stateMachine = Liu_StateMachine.getInstance();

        ArrayList<AbstractCard> cards = new ArrayList<>();

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.FengZhiLiu)) {
            cards.add(new FZL_YaZhi());
            cards.add(new FZL_BaiHuaSha());
            cards.add(new FZL_QiuFengPo());
            cards.add(new FZL_WanYeBai());
            cards.add(new FZL_ZhiQie());
            cards.add(new FZL_LieFengZhan());
            cards.add(new FZL_HuiFengZhan());
            cards.add(new FZL_BaiHuaQiFang());
            //cards.add(new JianYu_Card());
            cards.add(new FZL_CuiKuLaXiu());
            cards.add(new FZL_XianFaZhiRen());
            cards.add(new FZL_QiuYeLianJian());
            cards.add(new FZL_ShaXinZhouQi());
            //cards.add(new FZL_FanShi_Card());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.XiaZhiLiu)) {
            cards.add(new XZL_JuQi());
            //cards.add(new XZL_QiHai());
            cards.add(new XZL_FengMo());
            cards.add(new XZL_ShuangJi());
            //cards.add(new XZL_BuPoFa());
            cards.add(new XZL_ZiDianQingShuang());
            cards.add(new XZL_SaoDangQunMo());
            cards.add(new XZL_CaiCheQuMing());
            cards.add(new XZL_ZiXiaZhenQi());
            cards.add(new XZL_QiChongDouNiu());
            cards.add(new XZL_QiHuaWanQian());
            cards.add(new XZL_JuQue());
            cards.add(new XZL_ShengXie());
            cards.add(new XZL_ChunJun());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.YuZhiLiu)) {
            cards.add(new YuZL_FengYuBuDong());
            cards.add(new YuZL_QianLiMu());
            cards.add(new YuZL_QingShenZhui());
            cards.add(new YuZL_BaiLuYou());
            cards.add(new YuZL_YanGuiLai());
            cards.add(new YuZL_FeiYing());
            cards.add(new YuZL_JiShuiSanQianCard());
            //cards.add(new YuZL_YuYi());
            cards.add(new YuZL_YuChan());
            //cards.add(new GuoYanYunYan());
            //cards.add(new YuGan_Card());
            //cards.add(new XueLu_Card());
            cards.add(new YuZL_HouNiao());
            cards.add(new YuZL_BuSiNiao());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.YanZhiLiu)) {
            cards.add(new YanZL_YanLang());
            cards.add(new YanZL_YanJie());
            cards.add(new YanZL_LianHuan());
            cards.add(new YanZL_LuoXuanYan());
            //cards.add(new YanZL_ZhuoXinYan());
            cards.add(new YanZL_YanLiuJiXing());
            cards.add(new YanZL_HuiJinJianQi());
            cards.add(new YanZL_LiaoYuanJianQi());
            cards.add(new YanZL_ChunYangJianYi());
            cards.add(new YanZL_HuoYuJingShi());
            cards.add(new YanZL_HuoYuJianQi());
            cards.add(new YanZL_FenCheng());
            cards.add(new YanZL_ChiBi());
            cards.add(new YanZL_LieHuoChang());
        }

        return cards;
    }

    public static ArrayList<AbstractCard> getCardsFromLiuExcludeImportantCard(int flag) {
        Liu_StateMachine stateMachine = Liu_StateMachine.getInstance();

        ArrayList<AbstractCard> cards = getCardsFromLiu(flag);

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.FengZhiLiu)) {
            cards.removeIf((x) -> x.cardID.equals(FanShi_Card.ID));
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.YuZhiLiu)) {
            cards.removeIf((x) -> x.cardID.equals(YuZL_BuSiNiao.ID));
        }

        return cards;
    }

    public static AbstractCard getRandomCardFromLiu(Liu_StateMachine.StateEnum stateEnum) {
        ArrayList cards = getCardsFromLiu(stateEnum.getValue());
        if (cards.isEmpty()) {
            return null;
        }
        return getRandomCardsFromList(cards, false);
    }

    public static AbstractCard getRandomCardsFromList(ArrayList<AbstractCard> cards, boolean remove) {

        if (cards == null || cards.isEmpty()) {
            return null;
        }

        int index = AbstractDungeon.cardRandomRng.random(cards.size() - 1);
        AbstractCard card = cards.get(index);
        if (remove) {
            cards.remove(index);
        }
        return card;
    }

    public static <T> T getRandomElementFromList(ArrayList<T> list, boolean remove) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        int index = AbstractDungeon.cardRandomRng.random(list.size() - 1);
        T result = list.get(index);
        if (remove) {
            list.remove(index);
        }
        return result;
    }

    public static Liu_StateMachine.StateEnum getLiuFromCard(AbstractCard card) {

        if (card == null || !(card instanceof ILiuCard)) {
            return Liu_StateMachine.StateEnum.None;
        }

        return ((ILiuCard) card).getLiu();
    }

    public static AbstractMonster getRandomAliveMonster() {
        ArrayList<AbstractMonster> monsters = getAllAliveMonsters();

        AbstractMonster result = null;

        if (monsters.size() > 0) {
            result = Utils.getRandomElementFromList(monsters, false);
        }

        return result;
    }

    public static ArrayList<AbstractMonster> getAllAliveMonsters() {
        ArrayList<AbstractMonster> result = new ArrayList<>();
        MonsterGroup g = AbstractDungeon.getMonsters();
        if (g == null) {
            return result;
        }

        Iterator iterator = g.monsters.iterator();
        AbstractMonster m;
        while (iterator.hasNext()) {
            m = (AbstractMonster) iterator.next();
            if (m.isDead || m.isDying || m.currentHealth <= 0 || m.halfDead) {
                continue;
            }
            result.add(m);
        }
        return result;
    }


    public static List<AbstractMonster> sortMonsterByXPos(List<AbstractMonster> monsters) {
        List<AbstractMonster> sortedMonsters = monsters.stream()
                .filter(m -> !m.isDeadOrEscaped())
                .sorted(Comparator.comparing(m -> m.hb.cX))
                .collect(Collectors.toList());
        return sortedMonsters;
    }


    public static void effectJianShe(AbstractCreature source, AbstractMonster center, int damage) {
        //按X坐标排序存活怪物
        List<AbstractMonster> sortedMonsters = sortMonsterByXPos(AbstractDungeon.getMonsters().monsters);

        int centerIndex = -1;
        for (int i = 0; i < sortedMonsters.size(); i++) {
            if (sortedMonsters.get(i) == center) {
                centerIndex = i;
                break;
            }
        }

        if (centerIndex != -1) {
            if (centerIndex > 0) { // 左侧
                AbstractMonster m = sortedMonsters.get(centerIndex - 1);
                if (Math.abs(m.hb_x - center.hb_x) < 400) {
                    tryJianSheToMonster(source, m, damage);
                }
            }
            if (centerIndex < sortedMonsters.size() - 1) { // 右侧
                AbstractMonster m = sortedMonsters.get(centerIndex + 1);
                if (Math.abs(m.hb_x - center.hb_x) < 200) {
                    tryJianSheToMonster(source, m, damage);
                }
            }
        }
    }

    static void tryJianSheToMonster(AbstractCreature source, AbstractMonster target, int damage) {
        Utils.giveBaoYanDamage(source, target, modifyDamageByRongRong(damage, target), DamageInfo.DamageType.NORMAL);
    }

    public static void giveAllMonsterBaoYanDamage(int baseDamage) {
        giveAllMonsterBaoYanDamage(baseDamage, DamageInfo.DamageType.NORMAL);
    }

    public static void giveAllMonsterBaoYanDamage(int baseDamage, DamageInfo.DamageType damageType) {
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
        int[] damages = new int[monsters.size()];

        AbstractCreature source = AbstractDungeon.player;

        AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(source, damages, damageType, AbstractGameAction.AttackEffect.FIRE, true));

        for (int i = 0; i < monsters.size(); i++) {
            AbstractMonster _m = monsters.get(i);
            damages[i] = Utils.modifyDamageByRongRong(baseDamage, _m);
            DamageInfo info = new DamageInfo(source, damages[i], damageType);
            AbstractDungeon.actionManager.addToBottom(new NotifyBaoYanDamageAction(_m, info));
        }

    }

    public static int modifyDamageByRongRong(int damage, AbstractCreature target) {
        if (target.hasPower(RongRong.POWER_ID)) {
            damage += target.getPower(RongRong.POWER_ID).amount;
        }
        return damage;
    }

    public static int stasticsAttackCardPlayedInTurn() {
        ArrayList<AbstractCard> cards = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        int d = 0;
        for (int i = 0; i < cards.size(); i++) {
            AbstractCard c = cards.get(i);
            if (c.type == AbstractCard.CardType.ATTACK) {
                d++;
            }
        }
        return d;
    }

    public static int stasticsCardPlayedInTurn(ICardFilter f) {
        ArrayList<AbstractCard> cards = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        int d = 0;
        for (int i = 0; i < cards.size(); i++) {
            AbstractCard c = cards.get(i);
            if (f == null || f.filter(c)) {
                d++;
            }
        }
        return d;
    }
}
