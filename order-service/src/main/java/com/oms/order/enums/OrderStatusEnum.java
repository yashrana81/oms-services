package com.oms.order.enums;

public enum OrderStatusEnum {
    ORDERED(1L, "ordered"),
    CANCELLED(2L, "cancelled"),
    SHIPPED(3L, "shipped"),
    DELIVERED(4L, "delivered");

    private final Long id;
    private final String name;

    OrderStatusEnum(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static OrderStatusEnum fromId(Long id) {
        for (OrderStatusEnum status : values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status id: " + id);
    }

    public static OrderStatusEnum fromName(String name) {
        if (name == null) {
            return null;
        }
        for (OrderStatusEnum status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}