/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.internal.OperationExecutor;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.codecs.configuration.CodecProvider;

public class MongoDatabaseImpl extends com.mongodb.client.internal.MongoDatabaseImpl implements eu.mcone.networkmanager.core.api.database.MongoDatabase {

    @Getter
    private Database database;

    MongoDatabaseImpl(MongoClient client, Database database, OperationExecutor operationExecutor) {
        super(
                database.getName(),
                client.getMongoClientOptions().getCodecRegistry(),
                client.getMongoClientOptions().getReadPreference(),
                client.getMongoClientOptions().getWriteConcern(),
                client.getMongoClientOptions().getRetryWrites(),
                client.getMongoClientOptions().getReadConcern(),
                operationExecutor
        );

        this.database = database;
    }

    @Override
    public MongoCollection getCollectionWithAdditionalCodecProviders(String name, CodecProvider... providers) {
        return MongoConnection.addCodecProviders(getCollection(name), providers);
    }

}
