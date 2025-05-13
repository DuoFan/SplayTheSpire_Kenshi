package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import game.duofan.kenshi.variable.*;

public class VariableRegister implements EditCardsSubscriber {

    @Override
    public void receiveEditCards() {
        BaseMod.addDynamicVariable(new QiAmount());
        BaseMod.addDynamicVariable(new PJAmount());
        BaseMod.addDynamicVariable(new OverPJAmount());
        BaseMod.addDynamicVariable(new LamageScaler());
        BaseMod.addDynamicVariable(new QiLamagePlus());
        BaseMod.addDynamicVariable(new VNameLiuAmount());
        BaseMod.addDynamicVariable(new ShengXieLamage());
        BaseMod.addDynamicVariable(new FengYuAmount());
    }
}
