package org.example.config;

import org.example.enums.DBType;

/**
 * @author liuwg-a
 * @date 2020/7/23 10:25
 * @description
 */
public class DBContextHolder {

    private static final ThreadLocal<DBType> DB_CONTEXT_HOLDER = new ThreadLocal<>();

    public static void set(DBType dbType) {
        DB_CONTEXT_HOLDER.set(dbType);
    }

    public static DBType get() {
        return DB_CONTEXT_HOLDER.get();
    }

    public static void clear() {
        DB_CONTEXT_HOLDER.remove();
    }
}
