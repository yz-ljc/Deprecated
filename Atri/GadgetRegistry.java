package top.yzljc.atri.blockbuster.gadget;

import org.bukkit.inventory.ItemStack;
import top.yzljc.atri.Atri;
import top.yzljc.atri.blockbuster.gadget.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class GadgetRegistry {

    private static final Map<String, Gadget> GADGETS = new HashMap<>();

    static {
        register(new ThrowableBomb());
        register(new HyperionGadget());
        register(new GyrokineticWandGadget());
        register(new FireVeilWandGadget());
        register(new BonzoStaffGadget());
        register(new NukeRemoteGadget());
        register(new SkyblockDice());
        register(new TerminatorGadget());
    }

    public static void register(Gadget gadget) {
        GADGETS.put(gadget.getId().toLowerCase(), gadget);
    }

    public static void registerAll(Atri plugin) {
        for (Gadget g : GADGETS.values()) {
            g.register(plugin);
        }
    }

    public static Gadget get(String id) {
        return id == null ? null : GADGETS.get(id.toLowerCase());
    }

    public static ItemStack createItem(String id, int amount) {
        Gadget g = get(id);
        return g == null ? null : g.createItem(amount);
    }

    public static Set<String> getIds() {
        return GADGETS.keySet();
    }
}
