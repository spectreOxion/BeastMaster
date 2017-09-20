package nu.nerd.beastmaster;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

// ----------------------------------------------------------------------------
/**
 * Represents a possible item drop.
 */
public class Drop {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param itemId the ID of the custom item.
     * @param dropChance the drop chance in the range [0.0, 1.0].
     * @param min the minimum number of drops.
     * @param max the maximum number of drops.
     */
    public Drop(String itemId, double dropChance, int min, int max) {
        _itemId = itemId;
        _dropChance = dropChance;
        _min = min;
        _max = max;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the item ID of this drop.
     * 
     * @return the item ID of this drop.
     */
    public String getItemId() {
        return _itemId;
    }

    // ------------------------------------------------------------------------
    /**
     * Set the type ID of the objective associated with this drop, or null if
     * this drop does not denote an objective.
     * 
     * @param objectiveType the type ID of the objective.
     */
    public void setObjectiveType(String objectiveType) {
        _objectiveType = objectiveType;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the type ID of the objective associated with this drop, or null if
     * this drop does not denote an objective.
     * 
     * @return the type ID of the objective associated with this drop, or null
     *         if this drop does not denote an objective.
     */
    public String getObjectiveType() {
        return _objectiveType;
    }

    // ------------------------------------------------------------------------
    /**
     * Load a custom drop from a configuration file section named after the
     * custom item ID.
     * 
     * @param section the configuration section.
     * @param logger the logger.
     */
    public boolean load(ConfigurationSection section, Logger logger) {
        _itemId = section.getName();
        _dropChance = section.getDouble("chance", 0.0);
        _min = section.getInt("min", 1);
        _max = section.getInt("max", Math.max(1, _min));
        _objectiveType = section.getString("objective");
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * Save this drop as a child of the specified parent configuration section.
     * 
     * @param parentSection the parent configuration section.
     * @param logger the logger.
     */
    public void save(ConfigurationSection parentSection, Logger logger) {
        ConfigurationSection section = parentSection.createSection(_itemId);
        section.set("chance", _dropChance);
        section.set("min", _min);
        section.set("max", _max);
        section.set("objective", _objectiveType);
    }

    // ------------------------------------------------------------------------
    /**
     * Return the probability of this drop, in the range [0.0,1.0].
     * 
     * @return the probability of this drop, in the range [0.0,1.0].
     */
    public double getDropChance() {
        return _dropChance;
    }

    // ------------------------------------------------------------------------
    /**
     * Generate a new ItemStack by selecting a random number of items within the
     * configured range.
     *
     * @return the ItemStack.
     * @throws IllegalArgumentException if the dropped item has one of the
     *         special item IDs.
     */
    public ItemStack generate() {
        Item item = BeastMaster.ITEMS.getItem(_itemId);
        if (item.isSpecial()) {
            throw new IllegalArgumentException("can't drop special item " + item.getId());
        }

        ItemStack result = item.getItemStack();
        if (result != null) {
            result = result.clone();
            result.setAmount(Util.random(_min, _max));
        }
        return result;
    }

    // ------------------------------------------------------------------------
    /**
     * Return a short description of this drop, suitable for display in-line.
     * 
     * The description does not include a full explanation of the dropped item;
     * only it's ID.
     * 
     * @return a short description of this drop, suitable for display in-line.
     */
    public String getShortDescription() {
        StringBuilder s = new StringBuilder();
        s.append(ChatColor.WHITE).append(_dropChance * 100).append("% ");

        Item item = BeastMaster.ITEMS.getItem(_itemId);
        if (!item.isSpecial()) {
            if (_min == _max) {
                s.append(_min);
            } else {
                s.append('[').append(_min).append(',').append(_max).append(']');
            }
            s.append(' ');
            ItemStack itemStack = item.getItemStack();
            s.append((itemStack == null) ? ChatColor.RED + "nothing"
                                         : ChatColor.YELLOW + _itemId);
        }
        return s.toString();
    }

    // ------------------------------------------------------------------------
    /**
     * Return a brief description of the drop ID, item, probability and count.
     * 
     * @return a brief description of the drop.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(ChatColor.YELLOW).append(_itemId).append(": ");
        if (_objectiveType != null) {
            s.append(ChatColor.GREEN).append("(objective: ").append(_objectiveType).append(") ");
        }
        s.append(ChatColor.WHITE).append(_dropChance * 100).append("% ");

        Item item = BeastMaster.ITEMS.getItem(_itemId);
        if (!item.isSpecial()) {
            if (_min == _max) {
                s.append(_min);
            } else {
                s.append('[').append(_min).append(',').append(_max).append(']');
            }
            s.append(' ');

            ItemStack itemStack = item.getItemStack();
            s.append((itemStack == null) ? ChatColor.RED + "nothing"
                                         : ChatColor.WHITE + Util.getItemDescription(itemStack));
        }
        return s.toString();
    }

    // ------------------------------------------------------------------------
    /**
     * The custom item ID.
     */
    protected String _itemId;

    /**
     * Drop chance, [0.0,1.0].
     */
    protected double _dropChance;

    /**
     * Minimum number of items in item stack.
     */
    protected int _min;

    /**
     * Maximum number of items in item stack.
     */
    protected int _max;

    /**
     * The type ID of the objective associated with this item, or null if this
     * drop does not denote an objective.
     */
    protected String _objectiveType;
} // class Drop