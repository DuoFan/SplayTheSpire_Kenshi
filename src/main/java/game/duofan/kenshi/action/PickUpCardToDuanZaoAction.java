package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.common.Utils;

import java.util.ArrayList;

public class PickUpCardToDuanZaoAction extends AbstractGameAction {

    IDoCard doCard;

    boolean initialized;

    public PickUpCardToDuanZaoAction(IDoCard _doCard, int _amount) {
        duration = 0.5f;
        doCard = _doCard;
        amount = _amount;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {

        if(!initialized){
            if (AbstractDungeon.player.hand.size() <= 0) {
                System.out.println("没有可锻造的卡牌");
                Utils.showToast("没有可以锻造的卡牌!");
                isDone = true;
                return;
            }

            initialized = true;
        }

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