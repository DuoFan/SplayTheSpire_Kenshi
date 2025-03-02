package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.BaiHongGuanRiAction;

import java.util.ArrayList;

public class JiShuiSanQian extends AbstractPower {
    static int idIndex;

    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(JiShuiSanQian.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean isGetBlock;
    AbstractGameAction upper;
    AbstractCard targetCard;

    public JiShuiSanQian(AbstractCreature owner, AbstractCard targetCard) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.targetCard = targetCard;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        isGetBlock = false;

        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        updateDescription();
    }

    public void updateDescription() {
        String description = DESCRIPTIONS[0];
        description = description.replace("[NAME]", targetCard.name);
        this.description = description;
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        super.onPlayCard(card, m);
        if (card == targetCard) {
            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
            if (actions != null && actions.size() > 0) {
                upper = actions.get(actions.size() - 1);
            } else {
                upper = null;
            }
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card == targetCard) {
            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
            AbstractPlayer p = AbstractDungeon.player;

            if (actions.size() > 0) {
                for (int i = actions.size() - 1; i >= 0; i--) {
                    AbstractGameAction a = actions.get(i);
                    if (a.equals(upper)) {
                        break;
                    }

                    if (a instanceof DamageAllEnemiesAction) {
                        ArrayList<AbstractMonster> monsters = Utils.getAllAliveMonsters();
                        for (int j = 0; j < monsters.size(); j++) {
                            AbstractMonster m = monsters.get(j);
                            Utils.insertAbstract(() -> {
                                if (m.lastDamageTaken > 0) {
                                    Utils.gainBlockTop(p, m.lastDamageTaken);
                                }
                            }, i + j + 1);
                        }
                    } else if (a instanceof DamageAction || a instanceof DamageRandomEnemyAction || a instanceof BaiHongGuanRiAction) {
                        if (a.target.equals(AbstractDungeon.player) || !(a.target instanceof AbstractMonster)) {
                            continue;
                        }
                        Utils.insertAbstract(() -> {
                            AbstractCreature m = a.target;
                            if (m.lastDamageTaken > 0) {
                                Utils.gainBlockTop(p, m.lastDamageTaken);
                            }
                        }, i + 1);
                    }
                }
            }

            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if (isPlayer) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
    }
}