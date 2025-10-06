package goorm.domain.buslog.application.service;

import goorm.domain.buslog.presentation.dto.request.BusFavoriteReq;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;

import java.util.List;

public interface BusLogService {

    void postBusLogSave(BusLogSaveReq req, String userId);

    List<BusLogAllRes> getBusLogAll(String memberId);

    void updateBusFavorite(BusFavoriteReq req);
}
