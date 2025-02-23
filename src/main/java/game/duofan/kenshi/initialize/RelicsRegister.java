package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import game.duofan.kenshi.relic.*;

public class RelicsRegister implements EditRelicsSubscriber {
    @Override
    public void receiveEditRelics() {

        BaseMod.addRelic(new ChiYou(), RelicType.SHARED);
        BaseMod.addRelic(new YingGui(), RelicType.SHARED);
        BaseMod.addRelic(new BaiHong(), RelicType.SHARED);
        BaseMod.addRelic(new YuHua(), RelicType.SHARED);
        BaseMod.addRelic(new YanXue(), RelicType.SHARED);
    }
}
