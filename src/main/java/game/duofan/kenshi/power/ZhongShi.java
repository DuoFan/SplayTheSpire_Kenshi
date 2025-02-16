package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.FreeAttackPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ZhongShi extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(ZhongShi.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    HashMap<AbstractCard, Integer> cards;

    public ZhongShi(AbstractCreature owner, int _amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = _amount;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);
        this.updateDescription();

        Utils.addToBotAbstract(() -> effect());
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        super.onCardDraw(card);
        if (card.type == AbstractCard.CardType.ATTACK && card.costForTurn > 0
        && !cards.containsKey(card)) {
            cards.put(card,card.costForTurn);
            card.setCostForTurn(0);
        }
    }

    protected void effect() {
        cards = new HashMap<>();
        for (int i = 0; i < AbstractDungeon.player.hand.size(); i++) {
            AbstractCard c = AbstractDungeon.player.hand.group.get(i);
            if (c.type == AbstractCard.CardType.ATTACK && c.costForTurn > 0
                    && !cards.containsKey(c)) {
                cards.put(c,c.costForTurn);
                c.setCostForTurn(0);
            }
        }
    }

    void restore() {

        if(cards == null){
            return;
        }

        Iterator<Map.Entry<AbstractCard,Integer>> e = cards.entrySet().iterator();

        while (e.hasNext()){
            Map.Entry<AbstractCard,Integer> entry = e.next();
            entry.getKey().setCostForTurn(entry.getValue());
        }
        cards = null;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (Shi_StateMachine.getInstance().isStateValid(Shi_StateMachine.StateEnum.ZhongShi)
        && card.type == AbstractCard.CardType.ATTACK) {
            forceDispose();
            Shi_StateMachine.getInstance().update();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        forceDispose();
        Shi_StateMachine.getInstance().reset();
    }

    public void forceDispose() {
        if(cards != null){
            Utils.addToBotAbstract(() ->{
                restore();
            });
        }
    }
}