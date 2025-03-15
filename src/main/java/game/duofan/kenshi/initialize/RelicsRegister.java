package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import game.duofan.kenshi.relic.*;

public class RelicsRegister implements EditRelicsSubscriber {
    @Override
    public void receiveEditRelics() {

        BaseMod.addRelic(new JianPuTuLu(), RelicType.SHARED);
        BaseMod.addRelic(new JianShuYaoLing(), RelicType.SHARED);
        BaseMod.addRelic(new YiQi(), RelicType.SHARED);
    }
}
