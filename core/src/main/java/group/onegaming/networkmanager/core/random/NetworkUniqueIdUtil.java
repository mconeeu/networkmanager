/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.core.random;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import group.onegaming.networkmanager.core.api.database.Database;
import group.onegaming.networkmanager.core.api.random.Random;
import group.onegaming.networkmanager.core.api.random.UniqueIdType;
import group.onegaming.networkmanager.core.api.random.UniqueIdUtil;

import java.util.HashSet;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class NetworkUniqueIdUtil implements UniqueIdUtil {

    private final MongoCollection<UniqueIdEntry> idsCollection;
    private final Random random;

    private final HashSet<String> idCache;

    public NetworkUniqueIdUtil(MongoDatabase mc1system) {
        if (mc1system.getName().equalsIgnoreCase(Database.SYSTEM.getName())) {
            this.idsCollection = mc1system.getCollection("unique_ids", UniqueIdEntry.class);
            this.idCache = new HashSet<>();
            this.random = new Random(6);
        } else
            throw new IllegalArgumentException("Could not initialize NetworkUniqueIdUtil. Given database is not mc1system!");
    }

    public String getUniqueKey(String category) {
        return getUniqueKey(category, UniqueIdType.STRING);
    }

    public String getUniqueKey(String category, UniqueIdType type) {
        String uniqueKey;

        do {
            uniqueKey = getUniqueKey(type);
        } while (idsCollection.find(and(eq("key", uniqueKey), eq("category", category))).first() != null);
        idsCollection.insertOne(new UniqueIdEntry(uniqueKey, category));

        return uniqueKey;
    }

    public String getTmpUniqueKey(UniqueIdType type, boolean cache) {
        String uniqueKey;
        do {
            uniqueKey = getUniqueKey(type);
        } while (idCache.contains(uniqueKey));

        if (cache) {
            idCache.add(uniqueKey);
        }

        return uniqueKey;
    }

    public void clearCache() {
        idCache.clear();
    }

    private String getUniqueKey(UniqueIdType type) {
        return type == UniqueIdType.STRING ? random.nextString() : String.format("%04d", random.nextInt(10000));
    }
}