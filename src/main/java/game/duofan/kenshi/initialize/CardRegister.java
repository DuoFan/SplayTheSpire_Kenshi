package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import game.duofan.kenshi.card.*;

public class CardRegister implements EditCardsSubscriber {

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new Strike());
        BaseMod.addCard(new Defend());

        BaseMod.addCard(new YueBu());
        BaseMod.addCard(new KongZhiJuLi());
        BaseMod.addCard(new BaiJiaZhiChang_Card());
        BaseMod.addCard(new JiYiXingTai_Card());

        BaseMod.addCard(new FZL_PiaoSiXue());
        BaseMod.addCard(new FZL_KuangFengJuanYe());
        BaseMod.addCard(new FZL_QianYeWu());
        BaseMod.addCard(new FZL_ZhiQie());
        BaseMod.addCard(new FZL_FengZhiXin());
    }
}

