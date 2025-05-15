package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.VerticalAuraEffect;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.BuSiNiaoZhiYu;
import game.duofan.kenshi.card.YuZL_BuSiNiao;

public class BuSiNiao extends AbstractPower {
    static final String POWER_ID = IDManager.getInstance().getID(BuSiNiao.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    int validCount = 0;

    public BuSiNiao(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        this.amount = amount;
        validCount = amount;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        validCount += stackAmount;
    }

    public void updateDescription() {
        String description = DESCRIPTIONS[0];
        this.description = String.format(description,YuZL_BuSiNiao.exhaustAmount ,validCount);
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();
        validCount = amount;
        updateDescription();
    }

    @Override
    public int onLoseHp(int damageAmount) {

        if (damageAmount <= 0) {
            return damageAmount;
        }

        if(validCount <= 0){
            return damageAmount;
        }

        if (exhaustCards()) {
            useAmount();
            return  0;
        }
        else{
            return damageAmount;
        }
    }

    boolean exhaustCards() {

        int exhaustAmount = YuZL_BuSiNiao.exhaustAmount;

        AbstractCard[] cards = new AbstractCard[exhaustAmount];
        CardGroup[] groups = new CardGroup[exhaustAmount];

        int exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(0, cards, groups, AbstractDungeon.player.drawPile);
        exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.discardPile);
        exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.hand);

        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.drawPile);
        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.discardPile);
        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.hand);

        if (exhaustIndex >= exhaustAmount) {
            for (int i = 0; i < cards.length; i++) {
                AbstractCard c = cards[i];
                CardGroup g = groups[i];
                if (c != null) {
                    addToTop(new ExhaustSpecificCardAction(c, g));
                }
            }
        }

        return exhaustIndex >= exhaustAmount;
    }

    int tryFillCardsWithBuSiNiaoZhiYu(int exhaustIndex, AbstractCard[] cards, CardGroup[] groups, CardGroup g) {
        int cardLength = cards.length;
        int index = 0;
        int size = g.size();
        while (exhaustIndex < cardLength && index < size) {
            AbstractCard c = g.group.get(index);
            if (c.cardID.equals(BuSiNiaoZhiYu.ID)) {
                cards[exhaustIndex] = c;
                groups[exhaustIndex] = g;
                exhaustIndex++;
            }
            index++;
        }
        return exhaustIndex;
    }

    int tryFillCardsWithoutBuSiNiaoZhiYu(int exhaustIndex, AbstractCard[] cards, CardGroup[] groups, CardGroup g) {
        int cardLength = cards.length;
        int index = 0;
        int size = g.size();
        while (exhaustIndex < cardLength && index < size) {
            AbstractCard c = g.group.get(index);
            if (!c.cardID.equals(BuSiNiaoZhiYu.ID)) {
                cards[exhaustIndex] = c;
                groups[exhaustIndex] = g;
                exhaustIndex++;
            }
            index++;
        }
        return exhaustIndex;
    }

    void useAmount() {
        flash();
        this.addToTop(new SFXAction("ATTACK_FIRE"));
        this.addToTop(new VFXAction(AbstractDungeon.player, new BorderLongFlashEffect(Color.SCARLET), 0.0F, true));
        validCount--;
        updateDescription();
    }
}