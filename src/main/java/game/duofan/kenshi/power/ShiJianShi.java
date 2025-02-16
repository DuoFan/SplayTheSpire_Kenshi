package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
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
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import game.duofan.common.*;
import game.duofan.kenshi.action.BaDaoZhanAction;

public class ShiJianShi extends AbstractPower {

    static int idIndex;

    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(ShiJianShi.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    AbstractCard targetCard;

    int gold;

    public ShiJianShi(AbstractCard t, AbstractMonster m, int _gold) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        targetCard = t;
        owner = m;
        this.type = PowerType.DEBUFF;
        gold = _gold;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public void updateDescription() {
        if (targetCard == null) {
            this.description = "试剑石出现了错误";
        }
        else if(owner.hasPower("Minion")){
            this.description = " #r[" + owner.name + "] " + "是一个爪牙，" + " #r无法造成斩杀! ";
        }
        else {
            this.description = "使用 #r[" + targetCard.name + "] " + "斩杀" + " #r[" + owner.name + "] " + " 以获得10 #r金币 ";
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        super.onAfterUseCard(card, action);

        if (card != targetCard || owner.hasPower("Minion")) {
            return;
        }

        Utils.addToTopAbstract(() -> {
            if (!owner.hasPower(ID)) {
                return;
            }
            if ((owner.isDying || owner.currentHealth <= 0) && !owner.halfDead) {
                AbstractDungeon.player.gainGold(this.gold);
                AbstractPlayer p = AbstractDungeon.player;
                for(int i = 0; i < this.gold; ++i) {
                    AbstractDungeon.effectList.add(new GainPennyEffect(p, owner.hb.cX, owner.hb.cY, p.hb.cX, p.hb.cY, true));
                }
                Utils.removePower(owner,ID);
            }
        });
    }
}
