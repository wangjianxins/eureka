package com.netflix.discovery.util;

import org.apache.commons.lang.math.RandomUtils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An alternative to {@link String#intern()} with no capacity constraints.
 *
 * @author Tomasz Bak
 */
public class StringCache {

    public static final int LENGTH_LIMIT = 38;

    private static final StringCache INSTANCE = new StringCache();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, WeakReference<String>> cache = new WeakHashMap<String, WeakReference<String>>();
    private final int lengthLimit;

    public StringCache() {
        this(LENGTH_LIMIT);
    }

    public StringCache(int lengthLimit) {
        this.lengthLimit = lengthLimit;
    }

    public String cachedValueOf(final String str) {
        if (str != null && (lengthLimit < 0 || str.length() <= lengthLimit)) {
            // Return value from cache if available
            try {
                lock.readLock().lock();
                WeakReference<String> ref = cache.get(str);
                if (ref != null) {
                    return ref.get();
                }
            } finally {
                lock.readLock().unlock();
            }

            // Update cache with new content
            try {
                lock.writeLock().lock();
                WeakReference<String> ref = cache.get(str);
                if (ref != null) {
                    return ref.get();
                }
                cache.put(str, new WeakReference<>(str));
            } finally {
                lock.writeLock().unlock();
            }
            return str;
        }
        return str;
    }

    public int size() {
        try {
            lock.readLock().lock();
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public static String intern(String original) {
        return INSTANCE.cachedValueOf(original);
    }

    public static void main(String[] args) {
        if (true) {
            String s = "qqq";
            INSTANCE.cachedValueOf(s);

            s = null;
            System.gc();

            System.out.println(INSTANCE.cache.get("qqq").get());

            return;
        }

        int size = 10000000;
        List<String> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
//            results.add(String.valueOf(i));
            results.add(String.valueOf(RandomUtils.nextInt(10)));
        }
        int scene = 1;
        if (scene == 1) {
//            String str = null;
            long now = System.currentTimeMillis();
            String str = null;
            for (String result : results) {
                str = result;
            }
            System.out.println(String.format("%s 消耗时间 %d", str, System.currentTimeMillis() - now));
            // 59 44 39 100W + 全部不同
            // 48 100W + 随机数（10个）
        } else if (scene == 2) {
            long now = System.currentTimeMillis();
            String str = null;
            for (String result : results) {
                str = result.intern();
            }
            System.out.println(String.format("%s 消耗时间 %d", str, System.currentTimeMillis() - now));
            // 53676 100W + 全部不同
            // 2837 100W + 随机数（10个）
        } else {
            // 预热
            StringCache cache = new StringCache();
            results.forEach(cache::cachedValueOf);

            long now = System.currentTimeMillis();
            String str = null;
            for (String result : results) {
                str = cache.cachedValueOf(result);
            }
            System.out.println(String.format("%s 消耗时间 %d", str, System.currentTimeMillis() - now));
            // 677 100W + 全部不同
            // 499 100W + 随机数（10个）
        }

    }

}
