package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditKeywordsSubscriber;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.Keyword;
import game.duofan.common.Const;
import game.duofan.common.Utils;

import java.nio.charset.StandardCharsets;

import static com.megacrit.cardcrawl.core.Settings.language;

public class KeywordRegister implements EditKeywordsSubscriber {
    //...省略
    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String lang = "zhs";
        if (language == Settings.GameLanguage.ZHS) {
            lang = "zhs";
        }

        String json = Gdx.files.internal("ExampleModResources/localization/keywords_" + lang + ".json")
                .readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                // 这个id要全小写
                BaseMod.addKeyword("duofan_kenshi", keyword.NAMES[0], keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }
}
