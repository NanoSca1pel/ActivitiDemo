package org.lht.springboot.enums;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2024年01月04日 9:05
 */
public enum DataSourceKey {
    MASTER("master"),
    SLAVE("slave");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private DataSourceKey(String name) {
        this.name = name;
    }

}
