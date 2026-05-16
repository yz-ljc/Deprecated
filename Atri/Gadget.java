package top.yzljc.atri.blockbuster.gadget;

import org.bukkit.inventory.ItemStack;
import top.yzljc.atri.Atri;

public interface Gadget {

    String getId();

    ItemStack createItem(int amount);

    default void register(Atri plugin) {}
}
