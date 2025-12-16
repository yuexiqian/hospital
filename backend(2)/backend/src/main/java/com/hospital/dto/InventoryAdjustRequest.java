// src/main/java/com/hospital/dto/InventoryAdjustRequest.java
package com.hospital.dto;

import lombok.Data;

/**
 * 药品库存增减 请求体
 */
@Data
public class InventoryAdjustRequest {

    /**
     * 要操作的库存记录 ID（drug_inventory.id）
     * 有两种用法：
     * 1）如果接口是 POST /api/pharmacist/inventory/adjust
     *    就在 body 里传这个 inventoryId；
     * 2）如果你用的是路径变量 /inventory/{id}/adjust，
     *    也可以不用这个字段，那就把 service 里对 getInventoryId() 的调用改成用方法参数 id。
     */
    private Long inventoryId;

    /**
     * 增加为正数，减少为负数
     */
    private Integer deltaQty;

    private Integer ChangeQty;

    /**
     * 备注（目前只做说明，不强制必填）
     */
    private String reason;
}
