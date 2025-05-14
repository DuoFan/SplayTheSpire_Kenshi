package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.FanShi;
import game.duofan.kenshi.power.Liu_StateMachine;
import game.duofan.kenshi.power.RongRong;
import game.duofan.kenshi.variable.LeXueAmount;

import java.util.ArrayList;
import java.util.HashSet;

public class LeXueAction extends AbstractGameAction {

    public LeXueAction() {

    }

    public void update() {
        isDone = true;

        AbstractPlayer p = AbstractDungeon.player;

        CardGroup drawPile = p.drawPile;
        if (drawPile == null) {
            return;
        }

        if (p.hasPower(FanShi.POWER_ID)) {
            drawPile = p.discardPile;
        }

        if (drawPile == null) {
            return;
        }

        CardGroup hand = p.hand;

        HashSet<Liu_StateMachine.StateEnum> liuSet = new HashSet<>();
        liuSet.add(Liu_StateMachine.StateEnum.None);

        if (hand != null) {
            for (int i = 0; i < hand.size(); i++) {
                AbstractCard c = p.hand.getNCardFromTop(i);
                liuSet.add(Utils.getLiuFromCard(c));
            }
        }

        for (int i = 0; i < drawPile.size(); i++) {
            AbstractCard c = drawPile.getNCardFromTop(i);
            if (liuSet.add(Utils.getLiuFromCard(c))) {
                Utils.playerDrawCardByFilterActionTop(1, (x) -> {
                    return x.equals(c);
                });
            }
        }
    }
}
