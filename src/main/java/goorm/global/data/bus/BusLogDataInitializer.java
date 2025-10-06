package goorm.global.data.bus;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

//@Component
@RequiredArgsConstructor
@Slf4j
public class BusLogDataInitializer {

    @PostConstruct
    @Transactional
    public void initializeBusLogs() {

    }

}
