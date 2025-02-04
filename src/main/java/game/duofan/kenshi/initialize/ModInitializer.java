package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import game.duofan.kenshi.initialize.CardRegister;
import game.duofan.kenshi.initialize.CharacterRegister;
import game.duofan.kenshi.initialize.KeywordRegister;

@SpireInitializer // 加载mod的注解
public class ModInitializer {

    // 注解需要调用的方法，必须写
    public static void initialize() {
        BaseMod.subscribe(new TextRegister());
        BaseMod.subscribe(new KeywordRegister());
        BaseMod.subscribe(new CardRegister());
        BaseMod.subscribe(new CharacterRegister());
        BaseMod.subscribe(new RelicsRegister());
    }

}
