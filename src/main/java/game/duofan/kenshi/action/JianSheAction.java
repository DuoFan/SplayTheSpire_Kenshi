package game.duofan.kenshi.action;//

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import game.duofan.common.Utils;
import game.duofan.kenshi.power.RongRong;

import java.util.ArrayList;

public class JianSheAction extends AbstractGameAction {

    AbstractCreature source;
    AbstractMonster center;
    int damage;

    public JianSheAction(AbstractCreature _source, AbstractMonster _center, int _damage) {
        source = _source;
        center = _center;
        damage = _damage;
        this.actionType = ActionType.DAMAGE;
    }

    public void update() {

        ArrayList<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;

        int index = monsters.indexOf(center);
        if (index >= 0) {
            tryDamageToMonster(monsters, index - 1);
            tryDamageToMonster(monsters, index + 1);
        }

        isDone = true;
    }

    void tryDamageToMonster(ArrayList<AbstractMonster> monsters, int index) {
        if (index < 0 || index >= monsters.size()) {
            return;
        }

        AbstractMonster monster = monsters.get(index);

        int _damage = damage;

        if (monster.hasPower(RongRong.POWER_ID)) {
            RongRong power = (RongRong) monster.getPower(RongRong.POWER_ID);
            _damage += power.amount;
        }

        addToBot(new DamageAction(monster, new DamageInfo(source, _damage, DamageInfo.DamageType.NORMAL)));
    }
}
