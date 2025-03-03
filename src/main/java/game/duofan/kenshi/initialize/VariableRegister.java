package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import game.duofan.kenshi.variable.LamageScaler;
import game.duofan.kenshi.variable.OverPJAmount;
import game.duofan.kenshi.variable.PJAmount;
import game.duofan.kenshi.variable.QiAmount;

public class VariableRegister implements EditCardsSubscriber {

    @Override
    public void receiveEditCards() {
        BaseMod.addDynamicVariable(new QiAmount());
        BaseMod.addDynamicVariable(new PJAmount());
        BaseMod.addDynamicVariable(new OverPJAmount());
        BaseMod.addDynamicVariable(new LamageScaler());
    }
}
