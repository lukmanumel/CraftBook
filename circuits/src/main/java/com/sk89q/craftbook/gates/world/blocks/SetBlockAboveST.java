package com.sk89q.craftbook.gates.world.blocks;

import org.bukkit.Server;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;
import com.sk89q.craftbook.ic.ICFactory;
import com.sk89q.craftbook.ic.RestrictedIC;
import com.sk89q.craftbook.ic.SelfTriggeredIC;

public class SetBlockAboveST extends SetBlockAbove implements SelfTriggeredIC {

    public SetBlockAboveST (Server server, ChangedSign sign, ICFactory factory) {
        super(server, sign, factory);
    }

    @Override
    public String getTitle () {

        return "Self-Triggered Set Block Above";
    }

    @Override
    public String getSignTitle () {

        return "SET BLOCK ABOVE ST";
    }

    public static class Factory extends SetBlockAbove.Factory implements RestrictedIC {

        public Factory (Server server) {

            super(server);
        }

        @Override
        public IC create (ChangedSign sign) {

            return new SetBlockAboveST(getServer(), sign, this);
        }
    }

    @Override
    public boolean isActive () {
        return true;
    }

    @Override
    public void think (ChipState chip) {
        onTrigger();
    }
}