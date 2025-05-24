package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import game.duofan.common.*;
import game.duofan.kenshi.effect.WeaveEffect;

import java.util.ArrayList;

public class LieHuoChang extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(LieHuoChang.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LieHuoChang(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = amount;

        String path128 = "img/powers/yanZL/lieHuoChang_84.png";
        String path48 = "img/powers/yanZL/lieHuoChang_32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        this.updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if (isPlayer) {
            flash();
            ArrayList<AbstractMonster> targets = Utils.getAllAliveMonsters();
            for (int i = 0; i < targets.size(); i++) {
                Utils.givePower(owner, targets.get(i), new RongRong(targets.get(i), amount));
            }
            Utils.addToBotAbstract(() ->{
                Utils.giveAllMonsterBaoYanDamage(1, DamageInfo.DamageType.NORMAL);
            });
        }
    }
}