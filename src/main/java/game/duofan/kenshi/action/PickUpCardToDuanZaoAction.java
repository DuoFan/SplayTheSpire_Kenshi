package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.IUpdateDescription;
import game.duofan.kenshi.power.LinkCardManager;

import java.util.ArrayList;
import java.util.Iterator;

public class PickUpCardToDuanZaoAction extends AbstractGameAction {

    IDoCard doCard;

    public PickUpCardToDuanZaoAction(IDoCard _doCard, int _amount) {
        duration = 0.5f;
        doCard = _doCard;
        amount = _amount;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {

        if (this.duration == 0.5F) {
            AbstractDungeon.handCardSelectScreen.open("进行锻造", 1, false, false, false, false, false);
            this.addToBot(new WaitAction(0.25F));
            this.tickDuration();
        } else {
            if (AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                this.tickDuration();
                return;
            }

            // 获取选择的卡牌
            ArrayList<AbstractCard> selectedCards = AbstractDungeon.handCardSelectScreen.selectedCards.group;

            // 处理卡牌并移回手牌
            for (AbstractCard card : selectedCards) {
                if (doCard != null) {
                    doCard.DoCard(card);
                }
                addToTop(new DuanZaoAction(card, amount));
                AbstractDungeon.player.hand.addToTop(card);
            }

            // 刷新手牌布局
            AbstractDungeon.player.hand.refreshHandLayout();

            // 标记卡牌已处理
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.clear();

            this.tickDuration();
        }
    }
}