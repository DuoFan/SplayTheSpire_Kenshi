package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.ManMa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ZhuoMoBuDing extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(ZhuoMoBuDing.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    ArrayList<Integer> damageBase;
    ArrayList<DamageInfo> damage;

    public ZhuoMoBuDing(AbstractMonster m) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = m;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();

        resetIntent();
    }

    void resetIntent() {
        if (owner == null) {
            return;
        }

        if (!(owner instanceof AbstractMonster)) {
            System.out.println("不能给" + owner.name + "添加" + name);
            Utils.removePower(owner, POWER_ID);
            return;
        }

        AbstractMonster m = (AbstractMonster) owner;

        damage = m.damage;
        if (m.damage != null) {

            damage = new ArrayList<>();
            for (int i = 0; i < m.damage.size(); i++) {
                damage.add(new DamageInfo(m, 0, DamageInfo.DamageType.NORMAL));
            }
            ArrayList temp = m.damage;
            m.damage = damage;
            damage = temp;
        }

        m.setMove((byte) 0, AbstractMonster.Intent.UNKNOWN, 0, 0, false);
        m.createIntent();
    }

    void restoreDamageInfo() {
        if (owner == null) {
            return;
        }

        if (!(owner instanceof AbstractMonster)) {
            return;
        }

        AbstractMonster m = (AbstractMonster) owner;
        m.damage = damage;
        damage = null;

        m.createIntent();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void atEndOfRound() {
        super.atEndOfRound();
        executeRandomAction();
        restoreDamageInfo();
        Utils.removePower(owner, POWER_ID);
    }

    void executeRandomAction() {
        int random = AbstractDungeon.cardRandomRng.random(0, 3);
        switch (random) {
            case 0:
                strengthenAction();
                return;
            case 1:
                healAction();
                return;
            case 2:
                blockAction();
                return;
            case 3:
                manMaAction();
                return;
        }
    }

    void strengthenAction() {
        Utils.gainPower(owner, new StrengthPower(owner, 2));
    }

    void healAction() {
        Utils.gainHeal(owner, (int) (owner.maxHealth * 0.1));
    }

    void blockAction() {
        Utils.gainBlock(owner, 20);
    }

    void manMaAction() {
        Utils.makeTempCardInDrawPileAction(new ManMa(), 1, true, true);
    }
}
