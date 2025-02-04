package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditStringsSubscriber;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;

public class TextRegister implements EditStringsSubscriber {
    @Override
    public void receiveEditStrings() {
        String lang;
        if (Settings.language == Settings.GameLanguage.ZHS) {
            lang = "ZHS";
        } else {
            lang = "ENG";
        }
        BaseMod.loadCustomStringsFile(CardStrings.class, "ExampleModResources/localization/" + lang + "/cards.json");
        // 这里添加注册本地化文本
        BaseMod.loadCustomStringsFile(CharacterStrings.class, "ExampleModResources/localization/" + lang + "/characters.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "ExampleModResources/localization/" + lang + "/relics.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "ExampleModResources/localization/" + lang + "/powers.json");
    }
}
