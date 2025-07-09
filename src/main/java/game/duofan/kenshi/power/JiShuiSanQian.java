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
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import game.duofan.common.IDManager;
import game.duofan.common.Utils;
import game.duofan.kenshi.action.BaiHongGuanRiAction;
import game.duofan.kenshi.card.XZL_ChunJun;
import game.duofan.kenshi.card.YanZL_FenCheng;
import game.duofan.kenshi.card.YanZL_YanJie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JiShuiSanQian extends AbstractPower {
    static final String ORIGIN_POWER_ID = IDManager.getInstance().getID(JiShuiSanQian.class);
    // 能力的本地化字段
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ORIGIN_POWER_ID);
    // 能力的名称
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    ArrayList<JiShuiSanQianExecutor> executors;

    HashMap<AbstractMonster, Boolean> monsterDieMap;

    public JiShuiSanQian(AbstractCreature owner) {
        this.name = NAME;
        this.ID = ORIGIN_POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        // 如果需要不能叠加的能力，只需将上面的Amount参数删掉，并把下面的Amount改成-1就行
        this.amount = -1;

        String path128 = "img/powers/yuZL/JiShuiSanQianPower84.png";
        String path48 = "img/powers/yuZL/JiShuiSanQianPower32.png";
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
        this.description = description;
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        super.onPlayCard(card, m);
        if (card.type == AbstractCard.CardType.ATTACK) {
            if (executors == null) {
                executors = new ArrayList<>();
            }
            JiShuiSanQianExecutor e = new JiShuiSanQianExecutor(card);
            executors.add(e);
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card.type == AbstractCard.CardType.ATTACK) {
            for (int i = 0; i < executors.size(); i++) {
                JiShuiSanQianExecutor e = executors.get(i);
                if (e.getTargetCard().equals(card)) {
                    executors.remove(i);
                    e.ExecuteBlockAction();
                    break;
                }
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if (isPlayer) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
    }

    class JiShuiSanQianExecutor {
        AbstractGameAction upper;
        AbstractCard targetCard;

        boolean isCheckEveryTime;
        boolean isCheckOnceMore;

        public AbstractCard getTargetCard() {
            return targetCard;
        }

        public JiShuiSanQianExecutor(AbstractCard _targetCard) {
            targetCard = _targetCard;

            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
            if (actions != null && actions.size() > 0) {
                upper = actions.get(actions.size() - 1);
            } else {
                upper = null;
            }
            isCheckEveryTime = targetCard instanceof YanZL_FenCheng;
            isCheckOnceMore = true;
            //isCheckOnceMore = targetCard instanceof YanZL_YanJie || targetCard instanceof XZL_ChunJun;
        }

        void ExecuteBlockAction() {

            if (monsterDieMap == null) {
                monsterDieMap = new HashMap<>();
            }

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

                                //标记为死亡的怪物不再判断
                                if (monsterDieMap.getOrDefault(m, false).equals(true)) {
                                    return;
                                }

                                if (m.lastDamageTaken > 0) {
                                    flash();
                                    monsterDieMap.put(m, m.isDeadOrEscaped() || m.currentHealth <= 0);
                                    int b = Math.max(1, m.lastDamageTaken / 2);
                                    Utils.gainBlockTop(p, b);
                                    Utils.addToTopAbstract(() -> {
                                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, false));
                                    });
                                }
                            }, i + j + 1);
                        }
                    } else if (a instanceof DamageAction || a instanceof DamageRandomEnemyAction || a instanceof BaiHongGuanRiAction) {
                        if (a.target.equals(AbstractDungeon.player) || !(a.target instanceof AbstractMonster)) {
                            continue;
                        }
                        Utils.insertAbstract(() -> {
                            AbstractCreature m = a.target;

                            //标记为死亡的怪物不再判断
                            if (monsterDieMap.getOrDefault(m, false).equals(true)) {
                                return;
                            }

                            if (m.lastDamageTaken > 0) {
                                flash();
                                monsterDieMap.put((AbstractMonster) m, m.isDeadOrEscaped() || m.currentHealth <= 0);
                                int b = Math.max(1, m.lastDamageTaken / 2);
                                Utils.gainBlockTop(p, b);
                                Utils.addToTopAbstract(() -> {
                                    AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, false));
                                });
                            }
                        }, i + 1);
                    }
                }
            }

            if (isCheckEveryTime || isCheckOnceMore) {
                isCheckOnceMore = false;
                if (actions != null && actions.size() > 0) {
                    upper = actions.get(actions.size() - 1);
                    Utils.addToBotAbstract(() -> {
                        ExecuteBlockAction();
                    });
                }
            }
        }
    }
}