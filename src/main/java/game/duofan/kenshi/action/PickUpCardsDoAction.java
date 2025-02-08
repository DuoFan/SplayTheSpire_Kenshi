package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import game.duofan.kenshi.action.IDoCard;

import java.util.ArrayList;

public class PickUpCardsDoAction extends AbstractGameAction {

    public String text;
    IDoCard action;

    public PickUpCardsDoAction(String _text, int amount, IDoCard _action) {
        text = _text;
        this.setValues(AbstractDungeon.player, source, amount);
        this.actionType = ActionType.CARD_MANIPULATION;
        this.action = _action;
    }

    public void update() {
        if (this.duration == 0.5F) {
            // 参数修正：允许取消选择，且不强制选择 exactlyAmount 张卡
            AbstractDungeon.handCardSelectScreen.open(text, this.amount, false, false, false, false, false);
            this.addToBot(new WaitAction(0.25F));
            this.tickDuration();
        } else {
            if (action == null || AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                this.tickDuration();
                return;
            }

            // 获取选择的卡牌
            ArrayList<AbstractCard> selectedCards = AbstractDungeon.handCardSelectScreen.selectedCards.group;

            // 处理卡牌并移回手牌
            for (AbstractCard card : selectedCards) {
                action.DoCard(card);  // 执行自定义操作
                AbstractDungeon.player.hand.addToTop(card); // 将卡牌重新加入手牌
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