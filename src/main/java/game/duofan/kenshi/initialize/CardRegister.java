package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import com.megacrit.cardcrawl.cards.AbstractCard;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.*;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.ArrayList;

public class CardRegister implements EditCardsSubscriber {

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new Strike());
        BaseMod.addCard(new Defend());

        BaseMod.addCard(new YueBu());
        BaseMod.addCard(new KongZhiJuLi());
        BaseMod.addCard(new BaiJiaZhiChang_Card());
        BaseMod.addCard(new QianBianWanHua());
        BaseMod.addCard(new JiYiXingTai_Card());

        BaseMod.addCard(new ManMa());
        BaseMod.addCard(new ChaPin());

        ArrayList<AbstractCard> liuCards = Utils.getCardsFromLiu(Liu_StateMachine.StateEnum.All.getValue());

        for (int i = 0; i < liuCards.size(); i++) {
            BaseMod.addCard(liuCards.get(i));
        }
    }
}

