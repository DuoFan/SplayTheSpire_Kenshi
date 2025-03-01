package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.card.BuSiNiaoZhiYu;

public class BuSiNiao extends AbstractPower {
    static int idIndex;
    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(BuSiNiao.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    int exhaustAmount;
    boolean upgraded;

    public BuSiNiao(AbstractCreature owner, int exhaustAmount, boolean upgraded) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        this.owner = owner;
        this.type = PowerType.BUFF;

        this.exhaustAmount = exhaustAmount;
        this.upgraded = upgraded;

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
        if (upgraded) {
            description = DESCRIPTIONS[1];
        }
        this.description = String.format(description, exhaustAmount);
    }

    @Override
    public int onLoseHp(int damageAmount) {
        if (damageAmount <= 0) {
            return damageAmount;
        }

        if (!upgraded) {
            exhaustCardsThenCheckNeedDeath();
            return 0;
        } else {
            if (damageAmount >= owner.currentHealth) {
                exhaustCardsThenCheckNeedDeath();
                return 0;
            } else {
                return super.onLoseHp(damageAmount);
            }
        }
    }

    void exhaustCardsThenCheckNeedDeath() {
        AbstractCard[] cards = new AbstractCard[exhaustAmount];
        CardGroup[] groups = new CardGroup[exhaustAmount];

        int exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(0, cards, groups, AbstractDungeon.player.drawPile);
        exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.discardPile);
        exhaustIndex = tryFillCardsWithBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.hand);

        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.drawPile);
        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.discardPile);
        exhaustIndex = tryFillCardsWithoutBuSiNiaoZhiYu(exhaustIndex, cards, groups, AbstractDungeon.player.hand);

        Utils.addToTopAbstract(() ->{
            AbstractPlayer p = AbstractDungeon.player;
            if(p != null && !p.isDead){
                int cardAmount = p.drawPile.size() + p.discardPile.size() + p.hand.size();
                if(cardAmount <= 0){
                    p.currentHealth = 0;
                    this.addToTop(new LoseHPAction(this.owner, this.owner, 99999));
                }
            }
        });

        for (int i = 0; i < cards.length; i++) {
            AbstractCard c = cards[i];
            CardGroup g = groups[i];
            if (c != null) {
                addToTop(new ExhaustSpecificCardAction(c,g));
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
}