package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import game.duofan.kenshi.relic.ChiYouXiang;

public class RelicsRegister implements EditRelicsSubscriber {
    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new ChiYouXiang(), RelicType.SHARED);
    }
}
