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

        ArrayList<AbstractCard> cards = Utils.getCardsFromLiu(Liu_StateMachine.StateEnum.All.getValue());

        cards.add(new QiHai());
        cards.add(new BuPoFa_Card());

        cards.add(new YuYi());
        cards.add(new DuiSha_Card());
        cards.add(new XueLu_Card());
        cards.add(new GuoYanYunYan());

        cards.add(new FanShi_Card());
        cards.add(new JianYu_Card());

        cards.add(new FeiGong());

        cards.add(new Strike());
        cards.add(new Defend());

        cards.add(new LeXue());
        cards.add(new JianQiao());
        cards.add(new GongFaShanZhuan_Card());
        cards.add(new XinSuiYiDong_Card());
        cards.add(new JiYiXingTai_Card());
        cards.add(new QianBianWanHua());
        cards.add(new ZhanYanLuo_Card());
        cards.add(new XiuLuo_Card());
        cards.add(new ZhengFeng());
        cards.add(new ShiJun());

        cards.add(new BuSiNiaoZhiYu());
        cards.add(new YangZhiRen());
        cards.add(new YinZhiRen());
        cards.add(new XiaoChenJianFa(0));

        cards.add(new ShiJianShi());
        cards.add(new ShuangJianHeBi_Card());
        cards.add(new BaoFu());
        cards.add(new TaYin());
        cards.add(new NiTai());
        cards.add(new YeBu());
        cards.add(new TuoTaiHuanGu());

        for (int i = 0; i < cards.size(); i++) {
            BaseMod.addCard(cards.get(i));
            //UnlockTracker.unlockCard(cards.get(i).cardID);
        }
    }
}

