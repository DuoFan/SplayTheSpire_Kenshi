package game.duofan.kenshi.action;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.BetterDrawPileToHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.YuZL_JiShuiSanQianCard;

import java.util.Iterator;

public class FeiGeChuanShuAction extends AbstractGameAction {
    String tip = "抽牌堆中没有技能卡！";
    String tip2 = "选择抽取%d张技能卡";

    public FeiGeChuanShuAction(int _amount) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.amount = _amount;
    }

    public void update() {
        AbstractPlayer p = AbstractDungeon.player;

        if (this.duration == this.startDuration) {
            if (p.drawPile.isEmpty()) {
                this.isDone = true;
                Utils.showToast(tip);
            } else {
                CardGroup temp = new CardGroup(CardGroupType.UNSPECIFIED);
                Iterator var6 = p.drawPile.group.iterator();

                while (var6.hasNext()) {
                    AbstractCard c = (AbstractCard) var6.next();
                    if (c.type == AbstractCard.CardType.SKILL) {
                        temp.addToTop(c);
                    }
                }

                if (temp.size() == 0) {
                    this.isDone = true;
                    Utils.showToast(tip);
                    return;
                }

                temp.sortAlphabetically(true);
                temp.sortByRarityPlusStatusCardType(false);
                String _tip = String.format(tip2,amount);
                AbstractDungeon.gridSelectScreen.open(temp, this.amount, true, _tip);
                this.tickDuration();
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                Iterator var1 = AbstractDungeon.gridSelectScreen.selectedCards.iterator();

                while (var1.hasNext()) {
                    AbstractCard c = (AbstractCard) var1.next();
                    if (p.hand.size() == 10) {
                        p.drawPile.moveToDiscardPile(c);
                        p.createHandIsFullDialog();
                    } else {
                        AbstractDungeon.player.drawPile.moveToHand(c);
                        c.triggerWhenDrawn();
                    }
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }

            this.tickDuration();
        }
    }
}