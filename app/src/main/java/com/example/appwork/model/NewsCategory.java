package com.example.appwork.model;

public enum NewsCategory {
    TOP("top", "热点"),
    TECH("shehui", "社会"),
    CAMPUS("guonei", "国内"),
    ENTERTAINMENT("yule", "娱乐"),
    SPORTS("tiyu", "体育"),
    FINANCE("caijing", "财经"),
    MILITARY("junshi", "军事");

    private final String type;
    private final String name;

    NewsCategory(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
