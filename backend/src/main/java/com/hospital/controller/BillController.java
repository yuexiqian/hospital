package com.hospital.controller;

import com.hospital.dto.BillDetailDTO;
import com.hospital.dto.BillListItemDTO;
import com.hospital.dto.BillsSummaryDTO;
import com.hospital.service.BillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    /**
     * 费用摘要：
     *  - 待缴费笔数
     *  - 待缴费金额
     *  - 历史已缴费总额
     *
     * GET /api/bills/summary?userId=1
     */
    @GetMapping("/summary")
    public BillsSummaryDTO getSummary(@RequestParam Long userId) {
        return billService.getSummaryByUser(userId);
    }

    /**
     * 待缴费用列表
     *
     * GET /api/bills/unpaid?userId=1
     */
    @GetMapping("/unpaid")
    public List<BillListItemDTO> listUnpaid(@RequestParam Long userId) {
        return billService.listUnpaidByUser(userId);
    }

    /**
     * 历史缴费列表（已缴）
     *
     * GET /api/bills/history?userId=1
     */
    @GetMapping("/history")
    public List<BillListItemDTO> listHistory(@RequestParam Long userId) {
        return billService.listHistoryByUser(userId);
    }

    /**
     * 某一条费用的详情 + 明细
     *
     * GET /api/bills/{billId}/details
     */
    @GetMapping("/{billId}/details")
    public BillDetailDTO getBillDetail(@PathVariable Long billId) {
        return billService.getBillDetail(billId);
    }

    /**
     * 模拟缴费：
     *  - 把此 bill 的 status 改为 PAID
     *  - 填充 paid_time、pay_method
     *
     * POST /api/bills/{billId}/pay
     */
    @PostMapping("/{billId}/pay")
    public void pay(@PathVariable Long billId) {
        billService.payBill(billId, "SELF_SERVICE");
    }
}
