package goorm.domain.buslog.application.service;

import goorm.domain.buslog.presentation.dto.request.BusFavoriteReq;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;

import java.util.List;

/**
 * 🚌 BusLogService
 *
 * 사용자의 버스 이동 기록, 즐겨찾기 상태 등을 관리하는 서비스 인터페이스입니다.
 * 컨트롤러 계층에서 호출되며, 실제 비즈니스 로직은 {@code BusLogServiceImpl}에서 구현됩니다.
 */
public interface BusLogService {

    /**
     * 🪧 버스 로그 저장
     * <p>
     * 사용자가 버스 이용 정보를 새로 저장합니다.
     * 즐겨찾기 여부는 기본적으로 false 상태로 생성됩니다.
     *
     * @param req    버스 로그 저장 요청 DTO (출발지, 도착지, 정류장 정보 등 포함)
     * @param userId 현재 인증된 사용자 ID
     * @throws goorm.global.infra.exception.error.GoormBusException
     *         사용자가 존재하지 않거나 저장 중 오류가 발생한 경우
     */
    void postBusLogSave(BusLogSaveReq req, String userId);

    /**
     * 📋 버스 로그 전체 조회
     * <p>
     * 특정 사용자의 모든 버스 로그를 조회합니다.
     * 각 로그에는 알림 여부 및 즐겨찾기 여부가 함께 포함됩니다.
     *
     * @param memberId 사용자 ID
     * @return 버스 로그 목록 (BusLogAllRes 리스트)
     * @throws goorm.global.infra.exception.error.GoormBusException
     *         사용자가 존재하지 않거나 관련 데이터가 없는 경우
     */
    List<BusLogAllRes> getBusLogAll(String memberId);

    /**
     * ⭐ 즐겨찾기 상태 변경
     * <p>
     * 해당 버스 로그의 즐겨찾기 상태를 토글합니다.
     * (활성화 ↔ 비활성화 전환)
     *
     * @param req 즐겨찾기 상태 변경 요청 DTO (busLogId 포함)
     * @throws goorm.global.infra.exception.error.GoormBusException
     *         버스 로그 또는 즐겨찾기 정보가 존재하지 않을 경우
     */
    void updateBusFavorite(BusFavoriteReq req);
}
