package com.picture.domain;

public class VisualDataType {
    private String name;    // 类别名
    private Integer value;  // 该类别数量

    public VisualDataType(){}
    public VisualDataType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "VisualDataType{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
