package org.burrow_studios.obelisk.core.cache;

import org.burrow_studios.obelisk.api.cache.TurtleSetView;
import org.burrow_studios.obelisk.api.entities.Turtle;
import org.burrow_studios.obelisk.core.ObeliskImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DelegatingTurtleCacheView<T extends Turtle> implements TurtleSetView<T> {
    private final TurtleCache<? super T> cache;
    private Set<Long> ids = ConcurrentHashMap.newKeySet();
    private final Class<T> contentType;

    public DelegatingTurtleCacheView(@NotNull TurtleCache<? super T> cache, Class<T> contentType) {
        this.cache = cache;
        this.contentType = contentType;
        this.cache.registerDelegator(this);
    }

    public @NotNull TurtleCache<? super T> getCache() {
        return this.cache;
    }

    @Override
    public @NotNull ObeliskImpl getAPI() {
        return this.cache.getAPI();
    }

    public Class<T> getContentType() {
        return contentType;
    }

    public @NotNull Set<Long> getIdsAsImmutaleSet() {
        return ids.stream()
                // remove 'ghost entities' as they would probably cause API errors
                .filter(id -> cache.get(id, contentType) != null)
                .collect(Collectors.toUnmodifiableSet());
    }

    public @NotNull Set<T> getAsImmutableSet() {
        return ids.stream()
                .map(id -> cache.get(id, contentType))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    public @NotNull Set<Long> getIdsAsImmutableSetRaw() {
        return Set.copyOf(this.ids);
    }

    @Override
    public @Nullable T get(long id) {
        if (!ids.contains(id)) return null;
        return cache.get(id, contentType);
    }

    @Override
    public int size() {
        return this.ids.size();
    }

    @Override
    public boolean isEmpty() {
        return this.ids.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        if (!(o instanceof Turtle turtle)) return false;
        if (!contentType.isInstance(turtle)) return false;
        return this.containsId(turtle.getId());
    }

    public boolean containsId(long id) {
        return this.ids.contains(id);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        Iterator<Long> idIterator = this.ids.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return idIterator.hasNext();
            }

            @Override
            public T next() {
                return cache.get(idIterator.next(), contentType);
            }
        };
    }

    public boolean add(T t) {
        this.cache.add(t);
        return this.ids.add(t.getId());
    }

    public boolean add(long id) {
        // no checks on the id to allow 'ghost entities' (usually entities that do not exist YET as they are still being built)
        return this.ids.add(id);
    }

    public boolean remove(Object o) {
        if (!(o instanceof Turtle turtle)) return false;
        return this.removeById(turtle.getId());
    }

    public boolean removeById(long id) {
        return this.ids.remove(id);
    }

    public boolean addAll(@NotNull Iterable<? extends T> c) {
        boolean changed = false;
        for (T t : c)
            changed = this.add(t) | changed;
        return changed;
    }

    public boolean addAllIds(@NotNull Iterable<Long> c) {
        boolean changed = false;
        for (Long id : c)
            if (id != null)
                changed = this.add(id) | changed;
        return changed;
    }

    public boolean retainIds(@NotNull Collection<Long> c) {
        return ids.retainAll(c);
    }

    public boolean removeAll(@NotNull Iterable<?> c) {
        boolean changed = false;
        for (Object o : c)
            changed = this.remove(o) | changed;
        return changed;
    }

    public void clear() {
        this.ids.clear();
    }

    public void overwrite(Set<T> content) {
        final Set<Long> newIds = ConcurrentHashMap.newKeySet();
        for (T entity : content)
            newIds.add(entity.getId());
        this.ids = newIds;
    }

    public void overwriteIds(Set<Long> ids) {
        final Set<Long> newIds = ConcurrentHashMap.newKeySet();
        newIds.addAll(ids);
        this.ids = newIds;
    }
}
