package game.duofan.kenshi.relic;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import game.duofan.common.IDManager;
import game.duofan.kenshi.power.*;

// 继承CustomRelic
public class JianPuTuLu extends CustomRelic {
    public static final String ID = IDManager.getInstance().getID(JianPuTuLu.class);
    // 图片路径（大小128x128，可参考同目录的图片）
    private static final String IMG_PATH = "ExampleModResources/img/relics/MyRelic.png";
    // 遗物未解锁时的轮廓。可以不使用。如果要使用，取消注释
    // private static final String OUTLINE_PATH = "ExampleModResources/img/relics/MyRelic_Outline.png";
    // 遗物类型
    private static final RelicTier RELIC_TIER = RelicTier.STARTER;
    // 点击音效
    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;

    public JianPuTuLu() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        // 如果你需要轮廓图，取消注释下面一行并注释上面一行，不需要就删除
        // super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(OUTLINE_PATH), RELIC_TIER, LANDING_SOUND);
    }

    // 获取遗物描述，但原版游戏只在初始化和获取遗物时调用，故该方法等于初始描述
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new JianPuTuLu();
    }

    @Override
    public void atTurnStart() {
        super.atTurnStart();
        Liu_StateMachine.getInstance().changeLiu(getRandomLiu());
    }

    Liu_StateMachine.StateEnum getRandomLiu()
    {
        Liu_StateMachine.StateEnum stateEnum = Liu_StateMachine.StateEnum.FengZhiLiu;
        int value = AbstractDungeon.cardRandomRng.random(1, 3);
        switch (value) {
            case 1:
                stateEnum = Liu_StateMachine.StateEnum.FengZhiLiu;
                break;
            case 2:
                stateEnum = Liu_StateMachine.StateEnum.XiaZhiLiu;
                break;
            case 3:
                stateEnum = Liu_StateMachine.StateEnum.YuZhiLiu;
                break;
            case 4:
                stateEnum = Liu_StateMachine.StateEnum.YanZhiLiu;
                break;
        }
        return stateEnum;
    }

}
