package game.duofan.kenshi.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.WZL_TaYin;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.ArrayList;

public class TaYinAction extends AbstractGameAction {
    boolean retrieveCard;
    WZL_TaYin self;
    boolean isUpgraded;

    public TaYinAction(WZL_TaYin _self,boolean _isUpgraded) {
        self = _self;
        retrieveCard = false;
        isUpgraded = _isUpgraded;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(this.generateCardChoices(), "选择1张加入手牌", true);
            this.tickDuration();
        } else {
            if (!this.retrieveCard) {
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();

                    if(self != null){
                        self.targetCard = disCard;
                    }

                    if (isUpgraded) {
                        disCard.setCostForTurn(0);
                    }

                    disCard.current_x = -1000.0F * Settings.xScale;
                    if (AbstractDungeon.player.hand.size() < 10) {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(disCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    } else {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(disCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    }

                    AbstractDungeon.cardRewardScreen.discoveryCard = null;
                }

                this.retrieveCard = true;
            }

            this.tickDuration();
        }
    }

    private ArrayList<AbstractCard> generateCardChoices() {
        ArrayList derp = new ArrayList();

        int flag = Liu_StateMachine.StateEnum.All.getValue();
        flag ^= Liu_StateMachine.StateEnum.WeiZhiLiu.getValue();
        ArrayList<AbstractCard> liuCards = Utils.getCardsFromLiu(flag);

        while(derp.size() != 3) {
            AbstractCard c = Utils.getRandomCardsFromList(liuCards,true);
            derp.add(c);
        }

        return derp;
    }
}