package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.EditCharactersSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.purple.FollowUp;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import game.duofan.common.Const;
import game.duofan.kenshi.KenShi;

// 这段代码不能编译
public class CharacterRegister implements EditCharactersSubscriber {
    // 人物选择界面按钮的图片
    private static final String MY_CHARACTER_BUTTON = "ExampleModResources/img/char/Character_Button.png";
    // 人物选择界面的立绘
    private static final String MY_CHARACTER_PORTRAIT = "ExampleModResources/img/char/Character_Portrait.png";
    // 攻击牌的背景（小尺寸）
    private static final String BG_ATTACK_512 = "ExampleModResources/img/512/bg_attack_512.png";
    // 能力牌的背景（小尺寸）
    private static final String BG_POWER_512 = "ExampleModResources/img/512/bg_power_512.png";
    // 技能牌的背景（小尺寸）
    private static final String BG_SKILL_512 = "ExampleModResources/img/512/bg_skill_512.png";
    // 在卡牌和遗物描述中的能量图标
    private static final String SMALL_ORB = "ExampleModResources/img/char/small_orb.png";
    // 攻击牌的背景（大尺寸）
    private static final String BG_ATTACK_1024 = "ExampleModResources/img/1024/bg_attack.png";
    // 能力牌的背景（大尺寸）
    private static final String BG_POWER_1024 = "ExampleModResources/img/1024/bg_power.png";
    // 技能牌的背景（大尺寸）
    private static final String BG_SKILL_1024 = "ExampleModResources/img/1024/bg_skill.png";
    // 在卡牌预览界面的能量图标
    private static final String BIG_ORB = "ExampleModResources/img/char/card_orb.png";
    // 小尺寸的能量图标（战斗中，牌堆预览）
    private static final String ENEYGY_ORB = "ExampleModResources/img/char/cost_orb.png";
    public static final Color MY_COLOR = new Color(79.0F / 255.0F, 185.0F / 255.0F, 9.0F / 255.0F, 1.0F);


    public CharacterRegister() {
        Color characterRgbColor = Const.CHARACTER_RGB_COLOR;
        // 这里注册颜色
        BaseMod.addColor(Const.KENSHI_CARD_COLOR, characterRgbColor, characterRgbColor, characterRgbColor, characterRgbColor, characterRgbColor, characterRgbColor, characterRgbColor, BG_ATTACK_512, BG_SKILL_512, BG_POWER_512, ENEYGY_ORB, BG_ATTACK_1024, BG_SKILL_1024, BG_POWER_1024, BIG_ORB, SMALL_ORB);
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(new KenShi(CardCrawlGame.playerName), MY_CHARACTER_BUTTON, MY_CHARACTER_PORTRAIT, KenShi.CharacterEnum.CHARACTER_KENSHI);
    }
}
