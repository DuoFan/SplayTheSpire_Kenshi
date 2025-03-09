package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditStringsSubscriber;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;

public class TextRegister implements EditStringsSubscriber {
    @Override
    public void receiveEditStrings() {
        String lang;
        if (Settings.language == Settings.GameLanguage.ZHS) {
            lang = "ZHS";
        } else {
            lang = "ZHS";
        }
        // 这里添加注册本地化文本
        BaseMod.loadCustomStringsFile(CardStrings.class, "ExampleModResources/localization/" + lang + "/cards.json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class, "ExampleModResources/localization/" + lang + "/characters.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "ExampleModResources/localization/" + lang + "/relics.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "ExampleModResources/localization/" + lang + "/powers.json");
        BaseMod.loadCustomStringsFile(UIStrings.class, "ExampleModResources/localization/" + lang + "/ui.json");


    }
}
