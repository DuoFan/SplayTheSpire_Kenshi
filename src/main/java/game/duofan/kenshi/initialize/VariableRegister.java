package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import game.duofan.kenshi.variable.QiAmount;

public class VariableRegister implements EditCardsSubscriber {

    @Override
    public void receiveEditCards() {

        BaseMod.addDynamicVariable(new QiAmount());
    }
}
