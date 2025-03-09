package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.actions.common.AllEnemyApplyPowerAction;
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
import game.duofan.kenshi.action.WaitForFenChengAction;
import game.duofan.kenshi.card.YanZL_FenCheng;

import java.util.ArrayList;

public class SuSha extends AbstractPower {
    static int idIndex;

    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(SuSha.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    ArrayList<AbstractMonster> waitToCheckDieMonsters;
    AbstractGameAction upper;
    AbstractCard targetCard;
    int pobaiGive;
    boolean isFenCheng;

    public SuSha(AbstractCreature owner, AbstractCard targetCard, int _pobaiGive) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID + idIndex++;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.targetCard = targetCard;
        pobaiGive = _pobaiGive;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

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
        description = String.format(description, pobaiGive);
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
            isFenCheng = targetCard instanceof YanZL_FenCheng;
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card == targetCard) {
            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;

            if (actions.size() > 0) {
                for (int i = actions.size() - 1; i >= 0; i--) {
                    AbstractGameAction a = actions.get(i);
                    if (a.equals(upper)) {
                        break;
                    }

                    if (a instanceof DamageAllEnemiesAction || isFenCheng) {
                        waitToCheckDieMonsters = Utils.getAllAliveMonsters();
                        break;
                    } else if (a instanceof DamageAction || a instanceof DamageRandomEnemyAction || a instanceof BaiHongGuanRiAction) {
                        if (a.target.equals(AbstractDungeon.player) || !(a.target instanceof AbstractMonster)) {
                            continue;
                        }
                        AbstractMonster m = (AbstractMonster) a.target;
                        if (waitToCheckDieMonsters == null) {
                            waitToCheckDieMonsters = new ArrayList<>();
                        }
                        if (!waitToCheckDieMonsters.contains(m)) {
                            waitToCheckDieMonsters.add(m);
                        }
                    }
                }
            }

            Utils.addToBotAbstract(() -> {
                checkKillThenRemove();
            });
        }
    }

    void checkKillThenRemove() {
        if (!owner.hasPower(ID)) {
            return;
        }

        if (waitToCheckDieMonsters == null) {
            return;
        }
        boolean isKill = false;
        for (int i = 0; i < waitToCheckDieMonsters.size(); i++) {
            if (Utils.isKilledUnMinion(waitToCheckDieMonsters.get(i))) {
                isKill = true;
                break;
            }
        }
        if (isKill) {
            ArrayList<AbstractMonster> monsters = Utils.getAllAliveMonsters();
            AbstractPlayer p = AbstractDungeon.player;
            for (int i = 0; i < monsters.size(); i++) {
                AbstractMonster m = monsters.get(i);
                Utils.givePowerTop(p, monsters.get(i), new PoBai(m, pobaiGive));
            }
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        } else if (isFenCheng) {
            Utils.addToBotAbstract(() -> {
                checkKillThenRemove();
            });
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