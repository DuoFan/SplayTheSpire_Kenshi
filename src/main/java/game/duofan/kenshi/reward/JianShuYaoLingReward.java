package game.duofan.kenshi.reward;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.Liu_StateMachine;

import java.util.ArrayList;
import java.util.Iterator;

public class JianShuYaoLingReward extends CustomReward {
    public static final String[] TEXT;
    private static final Texture ICON;
    private static final UIStrings uiStrings;
    AbstractCard card;

    public JianShuYaoLingReward() {
        super(ICON, uiStrings.TEXT[0], RewardTypeEnum.JIAN_SHU_YAO_LING_REWARD);
        this.cards = getRewardCards();
        this.isDone = false;
    }

    public boolean claimReward() {
        if (AbstractDungeon.player.hasRelic("Question Card")) {
            AbstractDungeon.player.getRelic("Question Card").flash();
        }

        if (AbstractDungeon.player.hasRelic("Busted Crown")) {
            AbstractDungeon.player.getRelic("Busted Crown").flash();
        }

        if (AbstractDungeon.screen == CurrentScreen.COMBAT_REWARD) {
            AbstractDungeon.cardRewardScreen.open(this.cards, this, TEXT[0]);
            AbstractDungeon.previousScreen = CurrentScreen.COMBAT_REWARD;
        }

        return false;
    }

    ArrayList<AbstractCard> getRewardCards() {
        ArrayList<AbstractCard> rewardCards = new ArrayList();

        AbstractPlayer player = AbstractDungeon.player;
        int numCards = 3;

        AbstractRelic r;
        for (Iterator var2 = player.relics.iterator(); var2.hasNext(); numCards = r.changeNumberOfCardsInReward(numCards)) {
            r = (AbstractRelic) var2.next();
        }

        if (ModHelper.isModEnabled("Binary")) {
            --numCards;
        }

        ArrayList<AbstractCard> cards = Utils.getCardsFromLiu(Liu_StateMachine.StateEnum.All.getValue());

        while (rewardCards.size() < numCards) {
            AbstractCard.CardRarity rarity = AbstractCard.CardRarity.UNCOMMON;

            card = Utils.getRandomCardsFromList(cards, true);
            while (card.rarity != rarity) {
                card = Utils.getRandomCardsFromList(cards, true);
            }

            rewardCards.add(card.makeCopy());
        }

        Iterator iterator = rewardCards.iterator();

        while (iterator.hasNext()) {
            card = (AbstractCard) iterator.next();
            Iterator var12 = player.relics.iterator();

            while (var12.hasNext()) {
                AbstractRelic relic = (AbstractRelic) var12.next();
                relic.onPreviewObtainCard(card);
            }
        }
        return rewardCards;
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(IDManager.getInstance().getID(JianShuYaoLingReward.class));
        TEXT = uiStrings.TEXT;
        ICON = ImageMaster.loadImage("img/rewards/CardReward.png");
    }
}
