package game.duofan.kenshi.relic;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.defect.DiscardPileToHandAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import game.duofan.common.*;
import game.duofan.kenshi.action.DuanZaoHandAction;
import game.duofan.kenshi.power.*;

// 继承CustomRelic
public class JiXing extends CustomRelic implements IEventListener {
    public static final String ID = IDManager.getInstance().getID(JiXing.class);
    // 图片路径（大小128x128，可参考同目录的图片）
    private static final String IMG_PATH = "ExampleModResources/img/relics/MyRelic.png";
    // 遗物未解锁时的轮廓。可以不使用。如果要使用，取消注释
    // private static final String OUTLINE_PATH = "ExampleModResources/img/relics/MyRelic_Outline.png";
    // 遗物类型
    private static final RelicTier RELIC_TIER = RelicTier.UNCOMMON;
    // 点击音效
    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;

    public JiXing() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        // 如果你需要轮廓图，取消注释下面一行并注释上面一行，不需要就删除
        // super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(OUTLINE_PATH), RELIC_TIER, LANDING_SOUND);
    }

    // 获取遗物描述，但原版游戏只在初始化和获取遗物时调用，故该方法等于初始描述
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new JiXing();
    }

    @Override
    public void atBattleStart() {
        super.atBattleStart();
        EventManager.getInstance().registerToEvent(EventKey.ON_CARD_LINKED, this);
    }

    @Override
    public void OnEvent(Object sender, Object e) {
        LinkCardManager.CardLinkedContext context = (LinkCardManager.CardLinkedContext) e;

        if(context.self != null){
            Utils.addToBotAbstract(() ->{
                addToTop(new DiscardToHandAction(context.self));
            });
        }
    }
}
