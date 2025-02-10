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

public class PickUpCardToLinkAction extends AbstractGameAction {

    AbstractCard self;
    ArrayList<AbstractCard> linkableCards;
    boolean initialized;

    public PickUpCardToLinkAction(AbstractCard _self) {
        self = _self;
        duration = 0.5f;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if(!initialized){
            linkableCards = new ArrayList<>();
            Iterator<AbstractCard> i = AbstractDungeon.player.hand.group.iterator();
            while (i.hasNext()) {
                AbstractCard c = i.next();
                if (LinkCardManager.getInstance().canMakeLink(self, c)) {
                    linkableCards.add(c);
                }
            }

            if (linkableCards.size() <= 0) {
                System.out.println("没有可连锁的卡牌");
                Utils.showToast("没有可以连锁的卡牌!");
                isDone = true;
                return;
            }

            initialized = true;
        }
        if (this.duration == 0.5F) {
            // 参数修正：允许取消选择，且不强制选择 exactlyAmount 张卡
            AbstractDungeon.handCardSelectScreen.open("进行连锁", 1, false, false, false, false, false);
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
                if(LinkCardManager.getInstance().makeLink(self, card)){
                    if (self instanceof IUpdateDescription) {
                        IUpdateDescription d = (IUpdateDescription) self;
                        d.updateDescription();
                    }
                    AbstractDungeon.player.hand.addToTop(card);
                }
                else{
                    Utils.showToast("目标牌不可被这张" + self.name + "连锁!");
                    System.out.println("目标牌不可被这张" + self.name + "连锁!");
                }
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