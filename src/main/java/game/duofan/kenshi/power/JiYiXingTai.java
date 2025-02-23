package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.*;
import game.duofan.kenshi.card.YuZL_BuSiNiao;

import java.util.ArrayList;

public class JiYiXingTai extends AbstractPower implements IEventListener {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(JiYiXingTai.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean effectable;

    public JiYiXingTai(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = amount;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], amount);
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        EventManager.getInstance().registerToEvent(EventKey.ON_LIU_CHANGED, this);
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();
        effectable = true;
    }

    @Override
    public void OnEvent(Object sender, Object e) {
        if (effectable) {
            effectable = false;
            Liu_StateMachine.StateEnum liu = (Liu_StateMachine.StateEnum) e;
            ArrayList<AbstractCard> liuCards = Utils.getCardsFromLiu(liu.getValue());
            for (int i = 0; i < amount; i++) {
                AbstractCard c = Utils.getRandomCardsFromList(liuCards, false);
                if (c.cardID.equals(YuZL_BuSiNiao.ID)) {
                    liuCards.remove(c);
                    c = Utils.getRandomCardsFromList(liuCards, false);
                }
                c.setCostForTurn(0);
                Utils.makeTempCardInHand(c, 1);
            }
        }
    }
}
