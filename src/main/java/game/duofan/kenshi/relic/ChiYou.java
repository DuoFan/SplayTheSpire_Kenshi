package game.duofan.kenshi.relic;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import game.duofan.common.EventKey;
import game.duofan.common.EventManager;
import game.duofan.common.IDManager;
import game.duofan.kenshi.power.*;

import java.util.Iterator;

// 继承CustomRelic
public class ChiYou extends CustomRelic {
    public static final String ID = IDManager.getInstance().getID(ChiYou.class);
    // 图片路径（大小128x128，可参考同目录的图片）
    private static final String IMG_PATH = "ExampleModResources/img/relics/MyRelic.png";
    // 遗物未解锁时的轮廓。可以不使用。如果要使用，取消注释
    // private static final String OUTLINE_PATH = "ExampleModResources/img/relics/MyRelic_Outline.png";
    // 遗物类型
    private static final RelicTier RELIC_TIER = RelicTier.STARTER;
    // 点击音效
    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;

    public ChiYou() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        // 如果你需要轮廓图，取消注释下面一行并注释上面一行，不需要就删除
        // super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(OUTLINE_PATH), RELIC_TIER, LANDING_SOUND);
    }

    // 获取遗物描述，但原版游戏只在初始化和获取遗物时调用，故该方法等于初始描述
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new ChiYou();
    }

    @Override
    public void atBattleStart() {
        super.atBattleStart();

        EventManager.getInstance().removeAll_NotPersist_Event();

        EventManager.getInstance().notifyEvent(EventKey.ON_BATTLE_START, this, null);

        Iterator iterator = AbstractDungeon.getMonsters().monsters.iterator();

        AbstractMonster m = null;
        while (iterator.hasNext()) {
            m = (AbstractMonster) iterator.next();
            if (m.type == AbstractMonster.EnemyType.BOSS) {
                break;
            }
        }
        if ((m != null && m.type == AbstractMonster.EnemyType.BOSS)) {
            EventManager.getInstance().notifyEvent(EventKey.ON_BOSS_BATTLE_START, this, null);
        }

        this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new ZhuLiuBaiJia(AbstractDungeon.player)));
    }

    @Override
    public void atTurnStart() {
        super.atTurnStart();
        this.flash();
        EventManager.getInstance().notifyEvent(EventKey.ON_TURN_START, this, null);
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        super.onPlayCard(c, m);
        EventManager.getInstance().notifyEvent(EventKey.ON_CARD_PLAY, this, c);
    }
}
