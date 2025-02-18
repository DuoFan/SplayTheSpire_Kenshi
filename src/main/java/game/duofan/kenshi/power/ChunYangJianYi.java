package game.duofan.kenshi.power;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.OnObtainCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.DamageAllButOneEnemyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.BaDaoZhanAction;
import game.duofan.kenshi.action.NotifyBaoYanDamageAction;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ChunYangJianYi extends AbstractPower {
    public static final int RONG_RONG_GIVE = 2;

    // 能力的ID
    public static final String POWER_ID = IDManager.getInstance().getID(ChunYangJianYi.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    AbstractGameAction upper;
    AbstractCard targetCard;
    AbstractMonster targetMonster;

    public ChunYangJianYi(AbstractCreature owner, int amount) {
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

        effectForHand();
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], amount);
    }

    void effectForHand() {
        CardGroup hand = AbstractDungeon.player.hand;
        for (int i = 0; i < hand.size(); i++) {
            tryEffectForCard(hand.group.get(i));
        }
    }

    void tryEffectForCard(AbstractCard c) {
        if (c.type == AbstractCard.CardType.ATTACK) {
            BaoYanCardManager.getInstance().addCard(c);
        }
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        super.onCardDraw(card);
        tryEffectForCard(card);
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        super.onPlayCard(card, m);
        if (card.type == AbstractCard.CardType.ATTACK) {
            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
            if (actions.size() > 0) {
                upper = actions.get(actions.size() - 1);
            }
            targetCard = card;
            targetMonster = m;
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);

        if (card.equals(targetCard)) {
            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
            AbstractPlayer p = AbstractDungeon.player;
            HashSet<AbstractMonster> waitForRongRongMonsters = null;
            if (actions.size() > 0) {
                for (int i = actions.size() - 1; i >= 0; i--) {
                    AbstractGameAction a = actions.get(i);
                    if (a.equals(upper)) {
                        break;
                    }

                    if (a instanceof DamageAllEnemiesAction) {
                        if (waitForRongRongMonsters == null) {
                            waitForRongRongMonsters = new HashSet<>();
                        }

                        ArrayList<AbstractMonster> monsters = Utils.getAllAliveMonsters();
                        waitForRongRongMonsters.addAll(monsters);

                        try {
                            Field f = DamageAllEnemiesAction.class.getDeclaredField("utilizeBaseDamage");
                            f.setAccessible(true);
                            //不会重计算伤害
                            if (f.get(a).equals(false)) {
                                int[] damages = ((DamageAllEnemiesAction) a).damage;
                                for (int j = 0; j < damages.length; ++j) {
                                    damages[j] += RONG_RONG_GIVE;
                                    if (j < monsters.size()) {
                                        AbstractMonster m = monsters.get(i);
                                        DamageInfo info = new DamageInfo(p, damages[j]);
                                        actions.add(i + 1, new NotifyBaoYanDamageAction(m, info));
                                    }
                                }
                            }

                            System.out.println("--------------纯阳剑意修改DamageAllEnemiesAction伤害成功");
                        } catch (NoSuchFieldException e) {
                            System.out.println("--------------纯阳剑意修改DamageAllEnemiesAction伤害失败，不存在待修改字段");
                        } catch (IllegalAccessException e) {
                            System.out.println("--------------纯阳剑意修改DamageAllEnemiesAction伤害失败，无法访问待修改字段");
                        }
                    } else if (a instanceof DamageAction || a instanceof DamageRandomEnemyAction || a instanceof BaDaoZhanAction)
                    {

                        if (a.target.equals(AbstractDungeon.player) || !(a.target instanceof AbstractMonster)) {
                            continue;
                        }

                        if (waitForRongRongMonsters == null) {
                            waitForRongRongMonsters = new HashSet<>();
                        }
                        waitForRongRongMonsters.add((AbstractMonster) a.target);

                        try {
                            Field f = a.getClass().getDeclaredField("info");
                            f.setAccessible(true);
                            DamageInfo info = (DamageInfo) f.get(a);
                            info.output += RONG_RONG_GIVE;
                            int nextActionIndex = i + 1;
                            if(nextActionIndex >= actions.size() || !(actions.get(nextActionIndex) instanceof NotifyBaoYanDamageAction)) {
                                actions.add(nextActionIndex, new NotifyBaoYanDamageAction(a.target, info));
                            }
                            System.out.println("--------------纯阳剑意修改DamageAction或DamageRandomEnemyAction伤害成功");
                        } catch (NoSuchFieldException e) {
                            System.out.println("--------------纯阳剑意修改DamageAction或DamageRandomEnemyAction伤害失败，不存在待修改字段");
                        } catch (IllegalAccessException e) {
                            System.out.println("--------------纯阳剑意修改DamageAction或DamageRandomEnemyAction伤害失败，无法访问待修改字段");
                        }
                    }
                }
            }

            if (waitForRongRongMonsters != null) {
                Iterator<AbstractMonster> iterator = waitForRongRongMonsters.iterator();
                while (iterator.hasNext()) {
                    AbstractMonster m = iterator.next();
                    addToTop(new ApplyPowerAction(m, p, new RongRong(m, RONG_RONG_GIVE)));
                }
                waitForRongRongMonsters = null;
            }

            upper = null;
            targetCard = null;
            targetMonster = null;
            amount--;
            if (amount <= 0) {
                dispose();
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        dispose();
    }

    void dispose() {
        AbstractPlayer p = AbstractDungeon.player;
        removeCardsWithCardGroup(p.hand);
        removeCardsWithCardGroup(p.drawPile);
        removeCardsWithCardGroup(p.discardPile);
        AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(p, p, POWER_ID));
    }

    void removeCardsWithCardGroup(CardGroup g) {
        if (g == null) {
            return;
        }

        BaoYanCardManager baoYanCardManager = BaoYanCardManager.getInstance();

        for (int i = 0; i < g.size(); i++) {
            AbstractCard c = g.group.get(i);
            if (c.type == AbstractCard.CardType.ATTACK && !(c instanceof IBaoYanCard)) {
                baoYanCardManager.removeCard(c);
            }
        }
    }
}