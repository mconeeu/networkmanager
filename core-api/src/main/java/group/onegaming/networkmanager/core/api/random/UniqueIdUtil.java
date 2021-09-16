/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.core.api.random;

public interface UniqueIdUtil {

    /**
     * Creates a unique key with the passed category and the UniqueIdType STRING.
     *
     * @param category The category of the key
     * @return the generated unique key
     */
    String getUniqueKey(String category);

    /**
     * Generates a unique key with the passed category and UniqueIdType.
     *
     * @param category The category of the key
     * @param type     The UniqueIdType
     * @return the generated unique key
     */
    String getUniqueKey(String category, UniqueIdType type);

}
