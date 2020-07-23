package org.example.enums;

/**
 * @author liuwg-a
 * @date 2020/7/23 10:21
 * @description
 */
public enum DBType {
    db1("clickhouse-mysql"),
    db2("clickhouse-default");
    String description;
    DBType(String description) {
        this.description = description;
    }
}
