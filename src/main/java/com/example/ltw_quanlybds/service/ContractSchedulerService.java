package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Contract;
import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.repository.ContractRepository;
import com.example.ltw_quanlybds.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractSchedulerService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    // Chạy tự động vào lúc 00:01:00 (1 phút sau nửa đêm) mỗi ngày
    @Scheduled(cron = "0 1 0 * * ?")
//    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoExpireContracts() {
        System.out.println("--- Bắt đầu quét hợp đồng hết hạn ---");

        List<Contract> expiredContracts = contractRepository.findExpiredContracts();

        for (Contract contract : expiredContracts) {
            // 1. Chuyển hợp đồng thành "Kết thúc"
            contract.setStatus("Kết thúc");
            contractRepository.save(contract);

            // 2. Chuyển BĐS về trạng thái "Trống"
            Property property = contract.getProperty();
            if (property != null) {
                property.setStatus("Trống");
                propertyRepository.save(property);
            }

            System.out.println("Đã tự động kết thúc hợp đồng ID: " + contract.getId());
        }

        List<Contract> startingToday = contractRepository.findContractsStartingToday();
        for (Contract c : startingToday) {
            Property p = c.getProperty();
            if (p != null) {
                p.setStatus("Cho thuê");
                propertyRepository.save(p);
            }
        }

        System.out.println("--- Hoàn thành quét hợp đồng ---");
    }

}