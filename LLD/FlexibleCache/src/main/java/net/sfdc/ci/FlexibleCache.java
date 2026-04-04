package net.sfdc.ci;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

interface EvictionStrategy<K> {
    public K evict();
    public void add(K key);
}

class FIFOEvictionStrategy<K> implements EvictionStrategy<K> {
    private final List<K> fifoList = new LinkedList<>();

    @Override
    public K evict() {
        return fifoList.removeFirst();
    }

    @Override
    public void add(K key) {
        if(!fifoList.contains(key)) fifoList.addLast(key);
    }
}

class LRUEvictionStrategy<K> implements EvictionStrategy<K> {
    private final List<K> fifoList = new LinkedList<>();

    @Override
    public K evict() {
        return fifoList.removeFirst();
    }

    @Override
    public void add(K key) {
        int idx = fifoList.indexOf(key);
        if(idx != -1) {
            fifoList.remove(idx);
        }

        fifoList.addLast(key);
    }
}


interface DataStorage<K, V> {
    public boolean put(K key, V value);
    public void remove(K key);
    public V get(K key);
    public boolean contains(K key);
    public int size();


}

class MemoryDataStorage<K, V> implements DataStorage<K, V> {

    private final HashMap<K, V> map = new HashMap<>();

    @Override
    public boolean put(K key, V value) {
        map.put(key, value);
        return true;
    }

    @Override
    public void remove(K key) {
        this.map.remove(key);
    }

    @Override
    public @Nullable V get(K key) {
        if(!map.containsKey(key)) return null;
        return map.get(key);
    }

    @Override
    public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Override
    public int size() {
        return map.size();
    }
}

interface Cache<K, V> {
    public boolean put(K key, V value);
    public V get(K key);
    public boolean contains(K key);
    public int size();
}

public class FlexibleCache<K, V> implements Cache<K, V> {
    private final EvictionStrategy<K> evictionStrategy;
    private final DataStorage<K, V> dataStorage;
    private final int limit;


    public FlexibleCache(final EvictionStrategy<K> evictionStrategy, final DataStorage<K, V> dataStorage, final int limit) {
        this.evictionStrategy = evictionStrategy;
        this.dataStorage = dataStorage;
        this.limit = limit;
    }

    @Override
    public boolean put(K key, V value) {
        if(dataStorage.contains(key)) {
            evictionStrategy.add(key);
            return true;
        }

        if(dataStorage.size() < limit) {
            dataStorage.put(key, value);
            evictionStrategy.add(key);
            return true;
        }

        K keyToEvict = evictionStrategy.evict();
        this.dataStorage.remove(keyToEvict);

        evictionStrategy.add(key);
        this.dataStorage.put(keyToEvict, value);
        return true;
    }

    @Override
    public V get(K key) {
        if(!dataStorage.contains(key)) { return null; }
        return dataStorage.get(key);
    }

    @Override
    public boolean contains(K key) {
        return dataStorage.contains(key);
    }

    @Override
    public int size() {
        return dataStorage.size();
    }
}
