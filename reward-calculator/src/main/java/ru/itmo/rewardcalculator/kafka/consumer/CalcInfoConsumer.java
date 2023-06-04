package ru.itmo.rewardcalculator.kafka.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itmo.common.constant.KafkaTopics;
import ru.itmo.common.domain.message.CalcInfo;
import ru.itmo.common.service.util.BigDecimalUtil;
import ru.itmo.rewardcalculator.service.RewardService;
import ru.itmo.rewardcalculator.service.so.in.CalcInfoSO;

import java.math.BigInteger;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RequiredArgsConstructor
@Component
public class CalcInfoConsumer {
    @Getter
    @Setter
    private CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private CalcInfo.CalcInfoMessage payload;
    private final RewardService rewardService;

    @KafkaListener(topics = KafkaTopics.CALC_INFO)
    public void consume(byte[] message) throws InvalidProtocolBufferException {
        payload = CalcInfo.CalcInfoMessage.parseFrom(message);
        log.debug(
                """
                Timestamp: {}
                Calculation info:
                [
                    event_id: {}
                    amount {
                        scale: {}
                        precision: {}
                        value: {}
                    }
                    profile_id: {}
                    point_id: {}
                ]
                """,
                Instant.now().toString(), payload.getEventId(), payload.getAmount().getScale(),
                payload.getAmount().getPrecision(), new BigInteger(payload.getAmount().getValue().toByteArray()),
                payload.getProfileId(), payload.getPointId()
        );
        rewardService.calcRewardFor(new CalcInfoSO(
                payload.getEventId(),
                BigDecimalUtil.toJavaBigDecimal(payload.getAmount()),
                payload.getProfileId(),
                payload.getPointId()
        ));
        latch.countDown();
    }
}
