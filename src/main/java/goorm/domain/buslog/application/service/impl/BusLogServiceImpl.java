package goorm.domain.buslog.application.service.impl;

import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.busalarm.domain.repository.BusAlarmRepository;
import goorm.domain.buslog.application.service.BusLogService;
import goorm.global.infra.scheduled.StationService;
import goorm.domain.buslog.domain.entity.BusFavorite;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.domain.repository.BusFavoriteRepository;
import goorm.domain.buslog.presentation.dto.request.BusFavoriteReq;
import goorm.domain.member.domain.entity.Member;
import goorm.domain.member.domain.repository.MemberRepository;
import goorm.domain.buslog.presentation.dto.request.BusLogSaveReq;
import goorm.domain.buslog.presentation.dto.response.BusLogAllRes;
import goorm.domain.buslog.domain.repository.BusLogRepository;
import goorm.global.infra.exception.error.ErrorCode;
import goorm.global.infra.exception.error.GoormBusException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 🚌 BusLogServiceImpl
 *
 * <p>버스 이동 기록(BusLog) 및 즐겨찾기, 알림 관련 비즈니스 로직을 담당하는 구현체입니다.</p>
 * <ul>
 *   <li>사용자의 버스 이용 내역 저장</li>
 *   <li>버스 로그 전체 조회 (알림, 즐겨찾기 포함)</li>
 *   <li>즐겨찾기 상태 토글</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BusLogServiceImpl implements BusLogService {

    private final BusLogRepository busLogRepository;
    private final BusFavoriteRepository busFavoriteRepository;
    private final BusAlarmRepository busAlarmRepository;
    private final MemberRepository memberRepository;


    /**
     * 🪧 버스 로그 저장
     *
     * <p>새로운 버스 이용 로그를 생성 및 저장합니다.
     * 사용자가 존재하지 않으면 예외를 발생시킵니다.</p>
     *
     * <p>기본적으로 생성 시 즐겨찾기는 false, 알림은 별도의 로직에서 관리됩니다.</p>
     *
     * @param req    버스 로그 저장 요청 DTO
     * @param userId 현재 로그인된 사용자 ID
     * @throws GoormBusException 사용자가 존재하지 않을 경우
     */
    @Override
    public void postBusLogSave(BusLogSaveReq req, String userId) {
        Member findMember = memberRepository.findById(userId).orElse(null);
        if (findMember == null)
            throw new GoormBusException(ErrorCode.USER_NOT_EXIST);

        BusLog busLog = BusLog.builder()
                .member(findMember)
                .departure(req.departure())
                .destination(req.destination())
                .station(req.station())
                .notionId(req.notionId())
                .stationId(req.stationId())
                .build();

        BusAlarm busAlarm = BusAlarm.builder()
                .busLog(busLog)
                .build();

        BusFavorite busFavorite = BusFavorite.builder()
                .busLog(busLog)
                .build();
        busLogRepository.save(busLog);
        busFavoriteRepository.save(busFavorite);
        busAlarmRepository.save(busAlarm);
    }

    /**
     * 📋 버스 로그 전체 조회
     *
     * <p>특정 사용자의 모든 버스 로그를 조회합니다.
     * 각 로그마다 알림 활성화 여부 및 즐겨찾기 여부를 함께 반환합니다.</p>
     *
     * <p>사용자 또는 관련 데이터가 없을 경우 예외를 발생시킵니다.</p>
     *
     * @param memberId 사용자 ID
     * @return {@link BusLogAllRes} 리스트
     * @throws GoormBusException 사용자, 알림, 즐겨찾기 데이터가 존재하지 않을 경우
     */
    @Override
    public List<BusLogAllRes> getBusLogAll(String memberId) {
        Member findMember = memberRepository.findById(memberId).orElse(null);
        if (findMember == null)
            throw new GoormBusException(ErrorCode.USER_NOT_EXIST);

        List<BusLogAllRes> result = new ArrayList<>();
        result = selectV2(findMember, result);

        log.info("BusLog 전체 조회 완료: memberId={}, count={}", memberId, result.size());
        return result;
    }

    /**
     * ⭐ 즐겨찾기 상태 변경
     *
     * <p>버스 로그에 연결된 즐겨찾기 상태를 토글(활성/비활성)합니다.
     * 이미 true 상태면 비활성화하고, false면 활성화합니다.</p>
     *
     * @param req 즐겨찾기 상태 변경 요청 DTO
     * @throws GoormBusException 버스 로그 또는 즐겨찾기 정보가 존재하지 않을 경우
     */
    @Override
    public void updateBusFavorite(BusFavoriteReq req) {
        BusLog findBusLog = busLogRepository.findById(req.busLogId()).orElse(null);
        if (findBusLog == null)
            throw new GoormBusException(ErrorCode.BUS_LOG_NOT_EXIST);

        BusFavorite findBusFavorite = busFavoriteRepository.findByBusLog(findBusLog)
                .orElseThrow(() -> new GoormBusException(ErrorCode.BUS_FAVORITE_NOT_EXIST));

        if (findBusFavorite.isFavoriteFlag()) {
            findBusFavorite.deactivateIsFavoriteFlag();
        } else {
            findBusFavorite.activateIsFavoriteFlag();
        }
    }


    private List<BusLogAllRes> selectV1(Member findMember, List<BusLogAllRes> result) {
        List<BusLog> busLogs = busLogRepository.findByMember(findMember);

        for (BusLog busLog : busLogs) {
            BusAlarm findBusAlarm = busAlarmRepository.findByBusLog(busLog)
                    .orElseThrow(() -> new GoormBusException(ErrorCode.BUS_ALARM_NOT_EXIST));

            BusFavorite findBusFavorite = busFavoriteRepository.findByBusLog(busLog)
                    .orElseThrow(() -> new GoormBusException(ErrorCode.BUS_FAVORITE_NOT_EXIST));

            result.add(BusLogAllRes.of(
                    busLog,
                    findBusAlarm.isAlarmFlag(),
                    findBusFavorite.isFavoriteFlag()
            ));
        }

        return result;
    }


    private List<BusLogAllRes> selectV2(Member findMember, List<BusLogAllRes> result) {
        List<BusLog> busLogs = busLogRepository.findAllWithAlarmAndFavorite(findMember);

        return busLogs.stream()
                .map(busLog -> BusLogAllRes.of(
                        busLog,
                        busLog.getBusAlarm().isAlarmFlag(),
                        busLog.getBusFavorite().isFavoriteFlag()
                ))
                .toList();

    }
}
