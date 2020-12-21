/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.core.random;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
public class UniqueIdEntry {

    private final String key, category;

    @BsonCreator
    public UniqueIdEntry(
            @BsonProperty("key") String key,
            @BsonProperty("category") String category
    ) {
        this.key = key;
        this.category = category;
    }
}
