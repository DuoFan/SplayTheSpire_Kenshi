package game.duofan.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import game.duofan.kenshi.action.*;
import game.duofan.kenshi.card.*;
import game.duofan.kenshi.power.*;

import java.util.ArrayList;
import java.util.Iterator;

public class Utils {
    public static String generateID(String id) {
        return Const.PACKAGE_NAME + ":" + id;
    }

    public static void showToast(String s){
        AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F,  s, true));
    }

    public static void updateSZL_Description(IShanZhiLiuCard s){
        AbstractCard card = (AbstractCard) s;
        AbstractCard linked = LinkCardManager.getInstance().findLinkedCard(card);
        boolean isExchange = LinkCardManager.getInstance().isSetToExchange(card);

        CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(card.cardID);

        // 1. 获取基础描述
        String baseDesc = card.upgraded ? cardStrings.UPGRADE_DESCRIPTION : cardStrings.DESCRIPTION;

        // 2. 处理连锁状态
        if (linked != null) {
            // 替换 "连锁 1张卡牌" -> "已连锁: [卡牌名]"
            baseDesc = baseDesc.replace("duofan_kenshi:连锁 1张卡牌",
                    String.format(" NL 已连锁: *[%s]", linked.name));
        }

        // 3. 处理置换卡状态
        if (isExchange) {
            // 替换闪之流效果描述
            baseDesc = baseDesc.replace("duofan_kenshi:闪之流 :成为 duofan_kenshi:置换牌 并 duofan_kenshi:失去流派属性 。",
                    "duofan_kenshi:置换牌");
        }

        card.rawDescription = baseDesc;
        card.initializeDescription(); // 确保描述刷新
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

    public interface Lambda extends Runnable {
    }

    public static boolean isLiuCard(AbstractCard card){
        if (card == null) {
            return false;
        }

        return (card instanceof IFengZhiLiuCard)
                || (card instanceof IYingZhiLiuCard)
                || (card instanceof IXiaZhiLiuCard)
                || (card instanceof IWeiZhiLiuCard)
                || (card instanceof IShanZhiLiuCard);
    }

    public static void invokeLiuCardEffect(AbstractCard card) {
        if (card == null) {
            return;
        }

        Utils.addToBotAbstract(() ->{
            if (card instanceof IFengZhiLiuCard) {
                ((IFengZhiLiuCard) card).fengZhiLiuEffect();
            } else if (card instanceof IYingZhiLiuCard) {
                ((IYingZhiLiuCard) card).yingZhiLiuEffect();
            } else if (card instanceof IXiaZhiLiuCard) {
                ((IXiaZhiLiuCard) card).xiaZhiLiuEffect(false);
            } else if (card instanceof IWeiZhiLiuCard) {
                ((IWeiZhiLiuCard) card).weiZhiLiuEffect();
            } else if (card instanceof IShanZhiLiuCard) {
                IShanZhiLiuCard c = (IShanZhiLiuCard) card;
                if (c.effectable()) {
                    c.shanZhiLiuEffect();
                }
            }
            else if (card instanceof IDuanZhiLiuCard) {
                ((IDuanZhiLiuCard) card).duanZhiLiuEffect();
            }
            else if (card instanceof IYuZhiLiuCard) {
                ((IYuZhiLiuCard) card).yuZhiLiuEffect();
            }
        });
    }

    public static void invokeXZL_Effect(IXiaZhiLiuCard card,boolean isByQi) {
        if(card == null){
            return;
        }

        Utils.addToBotAbstract(() ->{
            card.xiaZhiLiuEffect(isByQi);
        });
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

    public static void giveDamage(AbstractCreature s, AbstractCreature t, int amount, DamageInfo.DamageType type) {
        if (s != null && t != null) {
            AbstractDungeon.actionManager.addToBottom(new
                    DamageAction(t, new DamageInfo(s, amount, type)));
        }
    }

    public static void givePower(AbstractCreature s, AbstractCreature t, AbstractPower power) {
        if (s != null && t != null) {
            AbstractDungeon.actionManager.addToBottom(new
                    ApplyPowerAction(t, s, power));
        }
    }

    public static void gainPower(AbstractCreature o, AbstractPower power) {
        givePower(o, o, power);
    }

    public static void removePower(AbstractCreature o, String powerID) {
        if (o != null) {
            AbstractDungeon.actionManager.addToBottom(new
                    RemoveSpecificPowerAction(o, o, powerID));
        }
    }

    public static void gainBlock(AbstractCreature o, int amount) {
        if (o == null) {
            return;
        }
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(o, amount));
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
        gainBlock(p, amount);
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

    public static void playerDrawCardByFilterAction(int amount, ICardFilter filter) {
        AbstractDungeon.actionManager.addToBottom(new DrawCardByFilterAction(amount, filter));
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
            cards.add(new FZL_PiaoSiXue());
            cards.add(new FZL_KuangFengJuanYe());
            cards.add(new FZL_QianYeWu());
            cards.add(new FZL_ZhiQie());
            cards.add(new FZL_HeFengZhan());
            cards.add(new FZL_LieFengZhan());
            cards.add(new FZL_FengZhiXin());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.YingZhiLiu)) {
            cards.add(new YZL_QianFu());
            cards.add(new YZL_YingShi());
            cards.add(new YZL_MeiYing());
            cards.add(new YZL_EZhao());
            cards.add(new YZL_YeBu());
            cards.add(new YZL_SiJiDaiFa());
            cards.add(new YZL_YingFu());
            cards.add(new YZL_YingZhiXin());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.XiaZhiLiu)) {
            cards.add(new XZL_JuQi());
            cards.add(new XZL_GuiYuan());
            cards.add(new XZL_BaiXiaZhan());
            cards.add(new XZL_HeQiZhan());
            cards.add(new XZL_PoXiao());
            cards.add(new XZL_YinYueYouQing());
            cards.add(new XZL_XiaZhiXin());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.WeiZhiLiu)) {
            cards.add(new WZL_NiTai());
            cards.add(new WZL_TaYin());
            cards.add(new WZL_DaoGuo());
            cards.add(new WZL_ChaoXi());
            cards.add(new WZL_GaiTouHuanMian());
            cards.add(new WZL_ZhaXiang());
            cards.add(new WZL_YangGong());
            cards.add(new WZL_WeiZhiXin());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.ShanZhiLiu)) {
            cards.add(new SZL_HuiXuan());
            cards.add(new SZL_ShanJi());
            cards.add(new SZL_KuaiFang());
            cards.add(new SZL_BaDaoZhan());
            cards.add(new SZL_FeiXing());
            cards.add(new SZL_ShanZhiXin());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.DuanZhiLiu)) {
            cards.add(new DZL_JiangXin());
            cards.add(new DZL_MoDao());
            cards.add(new DZL_ShiJianShi());
            cards.add(new DZL_QianChuiBaiLian());
        }

        if (stateMachine.hasLiuFlag(flag, Liu_StateMachine.StateEnum.YuZhiLiu)) {

            cards.add(new YuZL_YuHua());
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

    public static Liu_StateMachine.StateEnum getLiuFromCard(AbstractCard card) {

        if(card == null){
            return Liu_StateMachine.StateEnum.None;
        }

        if(card instanceof IFengZhiLiuCard){
            return Liu_StateMachine.StateEnum.FengZhiLiu;
        }
        else if(card instanceof IYingZhiLiuCard){
            return Liu_StateMachine.StateEnum.YingZhiLiu;
        }
        else if(card instanceof IXiaZhiLiuCard){
            return Liu_StateMachine.StateEnum.XiaZhiLiu;
        }
        else if(card instanceof IWeiZhiLiuCard){
            return Liu_StateMachine.StateEnum.WeiZhiLiu;
        }
        else if(card instanceof IShanZhiLiuCard){
            return Liu_StateMachine.StateEnum.ShanZhiLiu;
        }
        else if(card instanceof IDuanZhiLiuCard){
            return Liu_StateMachine.StateEnum.DuanZhiLiu;
        }
        else if(card instanceof IYuZhiLiuCard){
            return Liu_StateMachine.StateEnum.YuZhiLiu;
        }

        return Liu_StateMachine.StateEnum.None;
    }
}
