package game.duofan.kenshi.power;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class DisablePowerManager {
    static DisablePowerManager instance;

    public static DisablePowerManager getInstance() {
        if (instance == null) {
            instance = new DisablePowerManager();
        }
        return instance;
    }

    HashSet<DisablePower> powers;

    public void addDisablePower(DisablePower disablePower) {
        if (powers == null) {
            powers = new HashSet<>();
        }
        powers.add(disablePower);
    }

    public void removeDisablePower(DisablePower disablePower) {
        if (powers == null) {
            return;
        }
        powers.remove(disablePower);
    }

    public DisablePower findDisablePower(AbstractPower originPower) {
        return findDisablePower(originPower.owner, originPower.ID);
    }

    public DisablePower findDisablePower(AbstractCreature owner, String id) {
        if (powers == null) {
            return null;
        }

        DisablePower result = null;
        Iterator<DisablePower> i = powers.iterator();
        while (i.hasNext()) {
            DisablePower p = i.next();
            if (p.owner == owner && p.getOriginPower().ID.equals(id)) {
                result = p;
                break;
            }
        }
        return result;
    }
}
