package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.*;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.ArrayList;

public class CardRegister implements EditCardsSubscriber {

    @Override
    public void receiveEditCards() {

        ArrayList<AbstractCard> cards =Utils.getCardsFromLiu(Liu_StateMachine.StateEnum.All.getValue());
;
        cards.add(new Strike());
        cards.add(new Defend());

        cards.add(new QingJiu());
        cards.add(new XingJianLi_Card());
        cards.add(new YiJianHuiYou_Card());
        cards.add(new BaiJiaZhiChang_Card());
        cards.add(new XinSuiYiDong_Card());
        cards.add(new XinNianTongShen_Card());
        cards.add(new JiYiXingTai_Card());
        cards.add(new QianBianWanHua());
        cards.add(new QingLaiYouJia_Card());

        cards.add(new QinXi());

        cards.add(new FeiXing());
        cards.add(new BaDaoZhan());
        cards.add(new ShiJianShi());
        cards.add(new YangGong());
        cards.add(new GaiTouHuanMian());
        cards.add(new TaYin());
        cards.add(new NiTai());
        cards.add(new YeBu());

        for (int i = 0; i < cards.size(); i++) {
            BaseMod.addCard(cards.get(i));
            UnlockTracker.unlockCard(cards.get(i).cardID);
        }
    }
}

