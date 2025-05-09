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

public class BuSiNiao extends AbstractPower {
    static final String POWER_ID = IDManager.getInstance().getID(BuSiNiao.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    int exhaustAmount;

    public BuSiNiao(AbstractCreature owner, int exhaustAmount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        this.exhaustAmount = exhaustAmount;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public void updateDescription() {
        String description = DESCRIPTIONS[0];
        this.description = String.format(description, exhaustAmount);
    }

    @Override
    public int onLoseHp(int damageAmount) {

        if (damageAmount <= 0) {
            return damageAmount;
        }

        Utils.addToTopAbstract(() ->{
            checkRemove();
        });

        exhaustCards();
        useAmount();
        return 0;
    }

    void exhaustCards() {
        AbstractCard[] cards = new AbstractCard[exhaustAmount];
        CardGroup[] groups = new CardGroup[exhaustAmount];

        int exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(0, cards, groups, AbstractDungeon.player.drawPile);
        exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.discardPile);
        exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.hand);

        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.drawPile);
        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.discardPile);
        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.hand);

        for (int i = 0; i < cards.length; i++) {
            AbstractCard c = cards[i];
            CardGroup g = groups[i];
            if (c != null) {
                addToTop(new ExhaustSpecificCardAction(c, g));
            }
        }
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

    void checkRemove(){
        AbstractPlayer p = AbstractDungeon.player;
        int cardAmount = p.drawPile.size();
        cardAmount += p.discardPile.size();
        cardAmount += p.hand.size();

        if(cardAmount < 10){
            Utils.playRemovePowerTop(POWER_ID);
        }
    }

    void useAmount() {
        flash();
        this.addToTop(new SFXAction("ATTACK_FIRE"));
        this.addToTop(new VFXAction(AbstractDungeon.player, new BorderLongFlashEffect(Color.SCARLET), 0.0F, true));
    }
}