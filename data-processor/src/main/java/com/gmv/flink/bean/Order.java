package com.gmv.flink.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String orderId;
    private Timestamp orderTime;
    private Double price;
    private String shopId;
}
