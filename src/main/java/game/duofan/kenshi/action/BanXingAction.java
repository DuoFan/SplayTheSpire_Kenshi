package game.duofan.kenshi.action;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.SZL_BanXing;
import game.duofan.kenshi.power.IShanZhiLiuCard;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.Iterator;

public class BanXingAction extends AbstractGameAction {
    String tip = "抽牌堆中没有闪卡！";
    String tip2 = "选择抽取1张闪卡";

    SZL_BanXing self;

    public BanXingAction(SZL_BanXing card) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        self = card;
    }
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;

        if (this.duration == this.startDuration) {
            if (p.drawPile.isEmpty() || self == null) {
                this.isDone = true;
                Utils.showToast(tip);
            } else {
                CardGroup temp = new CardGroup(CardGroupType.UNSPECIFIED);
                Iterator var6 = p.drawPile.group.iterator();

                while (var6.hasNext()) {
                    AbstractCard c = (AbstractCard) var6.next();
                    if (Utils.getLiuFromCard(c).equals(Liu_StateMachine.StateEnum.ShanZhiLiu)) {
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
                AbstractDungeon.gridSelectScreen.open(temp, 1, tip2, false);
                this.tickDuration();
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                Iterator var1 = AbstractDungeon.gridSelectScreen.selectedCards.iterator();

                AbstractCard c = (AbstractCard) var1.next();
                self.targetCard = c;

                AbstractDungeon.player.drawPile.moveToHand(c);

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }

            this.tickDuration();
        }
    }
}
