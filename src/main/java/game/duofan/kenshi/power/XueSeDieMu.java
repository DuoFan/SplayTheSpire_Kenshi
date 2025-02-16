package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
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
import game.duofan.common.IDManager;
import game.duofan.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class XueSeDieMu extends AbstractPower {
    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(XueSeDieMu.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean isGetBlock;
    AbstractGameAction upper;

    public XueSeDieMu(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "ExampleModResources/img/powers/Example84.png";
        String path48 = "ExampleModResources/img/powers/Example32.png";
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(path48), 0, 0, 32, 32);

        isGetBlock = false;

        this.updateDescription();
    }

    public void flagGetBlock() {
        isGetBlock = true;
        updateDescription();
    }

    public void updateDescription() {
        String description = DESCRIPTIONS[0];
        if (isGetBlock) {
            description += " NL 获得等量于 #r未被格挡 的伤害相等的格挡。";
        }
        this.description = description;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card.baseBlock > 0 && card.block > 0) {
            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;

            boolean isValid = false;
            int damage = 0;
            int waitForGainBlock = 0;

            if (actions != null) {
                AbstractGameAction a;
                AbstractPlayer p = AbstractDungeon.player;
                for (int i = actions.size() - 1; i >= 0; i--) {
                    a = actions.get(i);

                    if (a.equals(upper)) {
                        break;
                    }

                    if (a instanceof GainBlockAction) {
                        if (a.target.equals(p)) {
                            damage += a.amount;
                            a.amount = 0;
                            isValid = true;
                        }
                    }
                }
            }

            if (!isValid) {
                return;
            }

            AbstractPlayer p = AbstractDungeon.player;
            Iterator iterator = AbstractDungeon.getMonsters().monsters.iterator();
            int monsterAmount = 0;
            AbstractMonster m;
            while (iterator.hasNext()) {
                m = (AbstractMonster) iterator.next();
                if (m.isDead || m.isDying || m.currentHealth <= 0 || m.halfDead) {
                    continue;
                }
                monsterAmount++;
                if (isGetBlock && damage > m.currentBlock) {
                    waitForGainBlock += Math.min(damage - m.currentBlock, m.currentHealth);
                }
            }
            int[] damages = new int[monsterAmount];
            Arrays.fill(damages, damage);
            this.addToBot(new DamageAllEnemiesAction(p, damages, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
            if (isGetBlock) {
                Utils.playerGainBlock(waitForGainBlock);
            }

            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        super.onPlayCard(card, m);
        if (card.baseBlock > 0 && card.block > 0) {
            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
            if (actions != null && actions.size() > 0) {
                upper = actions.get(actions.size() - 1);
            } else {
                upper = null;
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if (isPlayer) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }
}