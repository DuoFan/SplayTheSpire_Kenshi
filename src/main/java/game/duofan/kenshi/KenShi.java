package game.duofan.kenshi;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.helpers.SlimeAnimListener;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import game.duofan.common.*;
import game.duofan.kenshi.card.*;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.relic.JianPuTuLu;

import java.util.ArrayList;

// 继承CustomPlayer类
public class KenShi extends CustomPlayer {
    // 火堆的人物立绘（行动前）
    private static final String MY_CHARACTER_SHOULDER_1 = "img/camp/camp1.png";
    // 火堆的人物立绘（行动后）
    private static final String MY_CHARACTER_SHOULDER_2 = "img/camp/camp2.png";
    // 人物死亡图像
    private static final String CORPSE_IMAGE = "img/character/defeat.png";
    // 战斗界面左下角能量图标的每个图层
    private static final String[] ORB_TEXTURES = new String[]{
            "ExampleModResources/img/UI/orb/layer5.png",
            "ExampleModResources/img/UI/orb/layer4.png",
            "ExampleModResources/img/UI/orb/layer3.png",
            "ExampleModResources/img/UI/orb/layer2.png",
            "ExampleModResources/img/UI/orb/layer1.png",
            "ExampleModResources/img/UI/orb/layer6.png",
            "ExampleModResources/img/UI/orb/layer5d.png",
            "ExampleModResources/img/UI/orb/layer4d.png",
            "ExampleModResources/img/UI/orb/layer3d.png",
            "ExampleModResources/img/UI/orb/layer2d.png",
            "ExampleModResources/img/UI/orb/layer1d.png"
    };
    // 每个图层的旋转速度
    private static final float[] LAYER_SPEED = new float[]{-40.0F, -32.0F, 20.0F, -20.0F, 0.0F, -10.0F, -8.0F, 5.0F, -5.0F, 0.0F};
    // 人物的本地化文本，如卡牌的本地化文本一样，如何书写见下
    public static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(Utils.generateID(KenShi.class.getSimpleName()));

    int bodyTrack = 0;
    int hurtTrack = 1;

    Liu_StateMachine.StateEnum lastLiu;

    public KenShi(String name) {
        super(name, CharacterEnum.CHARACTER_KENSHI, ORB_TEXTURES, "ExampleModResources/img/UI/orb/vfx.png", LAYER_SPEED, null, null);

        // 人物对话气泡的大小，如果游戏中尺寸不对在这里修改（libgdx的坐标轴左下为原点）
        this.dialogX = (this.drawX + 0.0F * Settings.scale);
        this.dialogY = (this.drawY + 150.0F * Settings.scale);


        // 初始化你的人物，如果你的人物只有一张图，那么第一个参数填写你人物图片的路径。
        this.initializeClass(
                "ExampleModResources/img/char/character.png", // 人物图片
                MY_CHARACTER_SHOULDER_2, MY_CHARACTER_SHOULDER_1,
                CORPSE_IMAGE, // 人物死亡图像
                this.getLoadout(),
                0.0F, 0.0F,
                200.0F, 220.0F, // 人物碰撞箱大小，越大的人物模型这个越大
                new EnergyManager(3) // 初始每回合的能量
        );

        // 如果你的人物没有动画，那么这些不需要写
        this.loadAnimation("spineAnimation/Kenshi/stance_JK.atlas", "spineAnimation/Kenshi/stance_JK.json", 3F);
        AnimationState.TrackEntry e = this.state.setAnimation(bodyTrack, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(1.2F);

        lastLiu = Liu_StateMachine.StateEnum.None;
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.lastDamageTaken > 0) {
            playHurtAnim();
        }
    }

    public void playIdleAnim() {
        AnimationState.TrackEntry e = this.state.setAnimation(bodyTrack, "idle", true);
    }

    public void playHurtAnim() {
        AnimationState.TrackEntry e = this.state.setAnimation(hurtTrack, "hurt", false);
    }

    public void playLiuAnim(Liu_StateMachine.StateEnum liu) {
        exitLiuAnim();

        lastLiu = liu;
        switch (liu) {
            case FengZhiLiu:
                this.state.setAnimation(2, "leaf_idle", true);
                break;
            case XiaZhiLiu:
                this.state.setAnimation(3, "zhenqi_on", false).setListener(new AnimationState.AnimationStateListener() {
                    @Override
                    public void event(int i, Event event) {

                    }

                    @Override
                    public void complete(int i, int i1) {
                        state.setAnimation(3, "zhenqi_idle", true);
                    }

                    @Override
                    public void start(int i) {

                    }

                    @Override
                    public void end(int i) {

                    }
                });
                break;
            case YuZhiLiu:
                this.state.setAnimation(4, "yumao_idle", true);
                break;
            case YanZhiLiu:
                this.state.setAnimation(5, "sowrd_fire_on", false).setListener(new AnimationState.AnimationStateListener() {
                    @Override
                    public void event(int i, Event event) {

                    }

                    @Override
                    public void complete(int i, int i1) {
                        state.setAnimation(5, "sowrd_fire_idle", true);
                    }

                    @Override
                    public void start(int i) {

                    }

                    @Override
                    public void end(int i) {

                    }
                });
                break;
        }
    }

    private void exitLiuAnim() {
        switch (lastLiu){
            case FengZhiLiu:
                AnimationState.TrackEntry e1 = this.state.getCurrent(2);
                if(e1 != null){
                    e1.setLastTime(e1.getTime() % e1.getEndTime());
                    e1.setLoop(false);
                }
                break;
            case XiaZhiLiu:
                this.state.setAnimation(3, "zhenqi_off", false);
                break;
            case YuZhiLiu:
                AnimationState.TrackEntry e2 = this.state.getCurrent(4);
                if(e2 != null){
                    e2.setLastTime(e2.getTime() % e2.getEndTime());
                    e2.setLoop(false);
                }
                break;
            case YanZhiLiu:
                this.state.setAnimation(5, "sowrd_fire_off", false);
                break;
        }
        lastLiu = Liu_StateMachine.StateEnum.None;
    }

    // 初始卡组的ID，可直接写或引用变量
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        for (int x = 0; x < 5; x++) {
            retVal.add(Strike.ID);
        }
        for (int x = 0; x < 4; x++) {
            retVal.add(Defend.ID);
        }
        retVal.add(ShuangJianHeBi_Card.ID);

        return retVal;
    }

    // 初始遗物的ID，可以先写个原版遗物凑数
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(JianPuTuLu.ID);
        return retVal;
    }

    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(
                characterStrings.NAMES[0], // 人物名字
                characterStrings.TEXT[0], // 人物介绍
                70, // 当前血量
                70, // 最大血量
                0, // 初始充能球栏位
                99, // 初始携带金币
                5, // 每回合抽牌数量
                this, // 别动
                this.getStartingRelics(), // 初始遗物
                this.getStartingDeck(), // 初始卡组
                false // 别动
        );
    }

    // 人物名字（出现在游戏左上角）
    @Override
    public String getTitle(PlayerClass playerClass) {
        return characterStrings.NAMES[0];
    }

    // 你的卡牌颜色（这个枚举在最下方创建）
    @Override
    public AbstractCard.CardColor getCardColor() {
        return CharacterEnum.COLOR_KENSHI;
    }

    // 翻牌事件出现的你的职业牌（一般设为打击）
    @Override
    public AbstractCard getStartCardForEvent() {
        return new Strike();
    }

    // 卡牌轨迹颜色
    @Override
    public Color getCardTrailColor() {
        return Const.CHARACTER_RGB_COLOR;
    }

    // 高进阶带来的生命值损失
    @Override
    public int getAscensionMaxHPLoss() {
        return 5;
    }

    // 卡牌的能量字体，没必要修改
    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    // 人物选择界面点击你的人物按钮时触发的方法，这里为屏幕轻微震动
    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.play(AudioKey.SWORD_SOUND, 0.1f);
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }

    // 碎心图片
    @Override
    public ArrayList<CutscenePanel> getCutscenePanels() {
        ArrayList<CutscenePanel> panels = new ArrayList<>();
        // 有两个参数的，第二个参数表示出现图片时播放的音效
        panels.add(new CutscenePanel("img/ending/ending1.png", "ATTACK_MAGIC_FAST_1"));
        panels.add(new CutscenePanel("img/ending/ending2.png"));
        panels.add(new CutscenePanel("img/ending/ending3.png"));
        return panels;
    }

    // 自定义模式选择你的人物时播放的音效
    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return AudioKey.SWORD_SOUND;
    }

    // 游戏中左上角显示在你的名字之后的人物名称
    @Override
    public String getLocalizedCharacterName() {
        return characterStrings.NAMES[0];
    }

    // 创建人物实例，照抄
    @Override
    public AbstractPlayer newInstance() {
        return new KenShi(this.name);
    }

    // 第三章面对心脏说的话（例如战士是“你握紧了你的长刀……”之类的）
    @Override
    public String getSpireHeartText() {
        return characterStrings.TEXT[1];
    }

    // 打心脏的颜色，不是很明显
    @Override
    public Color getSlashAttackColor() {
        return Const.CHARACTER_RGB_COLOR;
    }

    // 吸血鬼事件文本，主要是他（索引为0）和她（索引为1）的区别（机器人另外）
    @Override
    public String getVampireText() {
        return Vampires.DESCRIPTIONS[0];
    }

    // 卡牌选择界面选择该牌的颜色
    @Override
    public Color getCardRenderColor() {
        return Const.CHARACTER_RGB_COLOR;
    }

    // 第三章面对心脏造成伤害时的特效
    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL};
    }

    @Override
    public void applyStartOfCombatLogic() {
        EventManager.getInstance().registerToEvent(EventKey.ON_LIU_CHANGED, new LiuAnimListener());
        EventManager.getInstance().registerToEvent(EventKey.ON_LIU_EXITED, new LiuExitListener());

        super.applyStartOfCombatLogic();
    }

    class LiuAnimListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            Liu_StateMachine.StateEnum stateEnum = (Liu_StateMachine.StateEnum) e;
            playLiuAnim(stateEnum);
        }
    }


    class LiuExitListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            exitLiuAnim();
        }
    }

    // 以下为原版人物枚举、卡牌颜色枚举扩展的枚举，需要写，接下来要用

    // 注意此处是在 MyCharacter 类内部的静态嵌套类中定义的新枚举值
    // 不可将该定义放在外部的 MyCharacter 类中，具体原因见《高级技巧 / 01 - Patch / SpireEnum》
    public static class CharacterEnum {
        // 修改为你的颜色名称，确保不会与其他mod冲突
        @SpireEnum
        public static PlayerClass CHARACTER_KENSHI;

        // ***将CardColor和LibraryType的变量名改为你的角色的颜色名称，确保不会与其他mod冲突***
        // ***并且名称需要一致！***
        @SpireEnum
        public static AbstractCard.CardColor COLOR_KENSHI;
    }

    public static class LibraryEnum {
        @SpireEnum
        public static CardLibrary.LibraryType COLOR_KENSHI;

    }
}
