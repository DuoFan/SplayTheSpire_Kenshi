package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.*;
import game.duofan.kenshi.card.SZL_ShanJi;
import game.duofan.kenshi.card.WZL_WeiZhiXin;

import java.util.ArrayList;

public class YanZhiXin extends AbstractPower implements IEventListener {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(ShanZhiXin.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean isRegister;

    public YanZhiXin(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);
        if (!isRegister) {
            Utils.addToBotAbstract(() -> {
                EventManager.getInstance().registerToEvent(EventKey.FIRST_YanZL_ON_TURN, this);
            });
            isRegister = true;
        }
    }

    @Override
    public void onVictory() {
        super.onVictory();
        EventManager.getInstance().unregisterFromEvent(EventKey.FIRST_YanZL_ON_TURN, this);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        EventManager.getInstance().unregisterFromEvent(EventKey.FIRST_YanZL_ON_TURN, this);
    }

    @Override
    public void OnEvent(Object sender, Object e) {
        YanZhiXinEffect();
    }

    public static void YanZhiXinEffect() {
        ArrayList<AbstractMonster> monsters = Utils.getAllAliveMonsters();
        AbstractPlayer p = AbstractDungeon.player;
        for (int i = 0; i < monsters.size(); i++) {
            AbstractMonster m = monsters.get(i);
            Utils.givePower(p, m, new RongRong(m, 1));
        }

        for (int i = 0; i < monsters.size(); i++) {
            AbstractMonster m = monsters.get(i);
            Utils.addToBotAbstract(() ->{
                Utils.giveBaoYanDamageInTop(p, m, Utils.modifyDamageByRongRong(1, m), DamageInfo.DamageType.NORMAL);
            });
        }
    }
}