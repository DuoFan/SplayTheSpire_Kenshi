package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import game.duofan.common.*;

public class JiangXinPingZhi extends AbstractPower {

    static int idIndex;

    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(JiangXinPingZhi.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    AbstractCard targetCard;

    int baseDamage = 12;

    int gold;

    DuanZaoListener listener;

    public JiangXinPingZhi(AbstractCard t,int _gold) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        owner = AbstractDungeon.player;
        targetCard = t;
        this.type = PowerType.BUFF;
        gold = _gold;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();

        listener = new DuanZaoListener();
        EventManager.getInstance().registerToEvent(EventKey.ON_CARD_BE_DUANZAO, listener);
    }

    public void updateDescription() {

        if (targetCard == null) {
            this.description = "这张卡找不到了";
        } else {
            switch (targetCard.type) {
                case ATTACK:
                    this.description = " #r[" + targetCard.name + "] " + "需满足基础伤害大于" + baseDamage + "，当前基础伤害为" + targetCard.baseDamage;
                    break;
                case SKILL:
                    this.description = " #r[" + targetCard.name + "] " + "需满足保留效果，当前" + (targetCard.selfRetain ? "已满足" : "未满足");
                    break;
                case POWER:
                    this.description = " #r[" + targetCard.name + "] " + "需满足费用为0，当前费用为" + targetCard.cost;
                    break;
                default:
                    this.description = " #r[" + targetCard.name + "] " + "无法满足匠心品质！";
                    break;
            }
        }
    }

    void checkIsOk() {
        boolean isOk = false;
        switch (targetCard.type) {
            case ATTACK:
                isOk = targetCard.baseDamage >= 12;
                break;
            case SKILL:
                isOk = targetCard.selfRetain;
                break;
            case POWER:
                isOk = targetCard.cost <= 0;
                break;
        }

        if (!isOk) {
            System.out.println(targetCard.name + "不满足匠心品质!");
        } else {
            System.out.println(targetCard.name + "满足匠心品质!");
            AbstractDungeon.player.gainGold(gold);
            AbstractPlayer p = AbstractDungeon.player;
            for(int i = 0; i < gold; ++i) {
                AbstractDungeon.effectList.add(new GainPennyEffect(p, p.hb.cX, p.hb.cY + 300, p.hb_x, p.hb.cY, true));
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if(isPlayer){
            checkIsOk();
            EventManager.getInstance().unregisterFromEvent(EventKey.ON_CARD_BE_DUANZAO, listener);
            listener = null;
            Utils.removePower(owner, this.ID);
        }
    }

    class DuanZaoListener implements IEventListener {

        @Override
        public void OnEvent(Object sender, Object e) {
            if (e.equals(targetCard)) {
                updateDescription();
            }
        }
    }

}
