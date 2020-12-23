package group.onegaming.networkmanager.core.api.database;

import com.mongodb.client.FindIterable;

public class MongoFindIterableSelector<T> {

    private final FindIterable<T> findIterable;

    public MongoFindIterableSelector(FindIterable<T> findIterable) {
        this.findIterable = findIterable;
    }

    public MongoFindIterableSelector<T> skip(int skip) {
        if (skip > 0) {
            findIterable.skip(skip);
        }

        return this;
    }

    public MongoFindIterableSelector<T> limit(int limit) {
        if (limit > 0) {
            findIterable.limit(limit);
        }

        return this;
    }

    public FindIterable<T> get() {
        return findIterable;
    }
}
