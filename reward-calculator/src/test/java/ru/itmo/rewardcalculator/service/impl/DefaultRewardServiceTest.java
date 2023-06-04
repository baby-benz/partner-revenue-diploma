package ru.itmo.rewardcalculator.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.itmo.common.domain.message.CalcInfo;
import ru.itmo.common.domain.message.FailedCalcInfo;
import ru.itmo.common.exception.HttpStatusCodeException;
import ru.itmo.common.service.util.BigDecimalUtil;
import ru.itmo.common.web.dto.response.calcscheme.CalcRule;
import ru.itmo.common.web.dto.response.calcscheme.CalcScheme;
import ru.itmo.common.domain.message.EventStatus;
import ru.itmo.common.web.client.CalcSchemeClient;
import ru.itmo.common.web.client.ProfilePointClient;
import ru.itmo.rewardcalculator.domain.entity.Reward;
import ru.itmo.rewardcalculator.domain.entity.RewardHistoryMetrics;
import ru.itmo.rewardcalculator.kafka.producer.CalculatedRewardProducer;
import ru.itmo.rewardcalculator.kafka.producer.EventStatusProducer;
import ru.itmo.rewardcalculator.kafka.producer.FailedEventProducer;
import ru.itmo.rewardcalculator.repository.RewardHistoryMetricsRepository;
import ru.itmo.rewardcalculator.repository.RewardRepository;
import ru.itmo.rewardcalculator.service.so.in.CalcInfoSO;
import ru.itmo.rewardcalculator.service.so.in.PeriodFilterSO;
import ru.itmo.rewardcalculator.service.so.out.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultRewardServiceTest {
    @Mock
    ProfilePointClient profilePointClient;
    @Mock
    CalcSchemeClient calcSchemeClient;
    @Mock
    RewardRepository rewardRepository;
    @Mock
    RewardHistoryMetricsRepository rewardHistoryMetricsRepository;
    @Mock
    EventStatusProducer eventStatusProducer;
    @Mock
    FailedEventProducer failedEventProducer;
    @Mock
    CalculatedRewardProducer calculatedRewardProducer;

    @Captor
    ArgumentCaptor<Reward> rewardCaptor;
    @Captor
    ArgumentCaptor<EventStatus.EventStatusMessage> eventStatusMessageCaptor;
    @Captor
    ArgumentCaptor<FailedCalcInfo.FailedCalcInfoMessage> failedCalcInfoMessageCaptor;

    @InjectMocks
    DefaultRewardService rewardService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(rewardService, "calculationExpressionRaw", "amount * interest_rate + bonus");
        ReflectionTestUtils.setField(rewardService, "amountCodeWord", "amount");
        ReflectionTestUtils.setField(rewardService, "interestRateCodeWord", "interest_rate");
        ReflectionTestUtils.setField(rewardService, "bonusCodeWord", "bonus");
    }

    @Test
    void when_calcRewardFor_then_ok() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        UUID eventId = UUID.randomUUID();

        CalcInfoSO calcInfo = new CalcInfoSO(eventId.toString(), BigDecimal.valueOf(44.44d),
                profileId.toString(), pointId.toString());

        CalcScheme calcScheme = new CalcScheme(calcSchemeId.toString(),
                List.of(new CalcRule(UUID.randomUUID().toString(), 0L, 0.1f, 0)), false);

        doReturn(calcSchemeId.toString()).when(profilePointClient).getCalcSchemeId(calcInfo.pointId());
        doReturn(calcScheme).when(calcSchemeClient).getCalcScheme(calcSchemeId.toString());

        rewardService.calcRewardFor(calcInfo);

        verify(rewardRepository).save(rewardCaptor.capture());
        verify(eventStatusProducer).sendMessage(eventStatusMessageCaptor.capture());

        Reward expectedReward = new Reward(
                eventId, BigDecimal.valueOf(44.44d), BigDecimal.valueOf(4.444d), BigDecimal.valueOf(1525.187d),
                "(44.44 * 0.1 + 0)", OffsetDateTime.now(), profileId, pointId, calcSchemeId
        );
        EventStatus.EventStatusMessage expectedEventStatusMessage = EventStatus.EventStatusMessage.newBuilder()
                .setEventId(eventId.toString())
                .setEventStatus(EventStatus.Status.PROCESSED)
                .build();

        Reward actualReward = rewardCaptor.getValue();

        assertAll(
                () -> assertEquals(expectedReward.getEventId(), actualReward.getEventId()),
                () -> assertEquals(expectedReward.getEventAmount(), actualReward.getEventAmount()),
                () -> assertEquals(expectedReward.getRewardAmount(), actualReward.getRewardAmount().setScale(3, RoundingMode.HALF_EVEN)),
                () -> assertEquals(expectedReward.getEventAmountTotal(), actualReward.getEventAmountTotal()),
                () -> assertEquals(expectedReward.getFormula(), actualReward.getFormula()),
                () -> assertEquals(expectedReward.getRewardTime().getYear(), actualReward.getRewardTime().getYear()),
                () -> assertEquals(expectedReward.getRewardTime().getMonth(), actualReward.getRewardTime().getMonth()),
                () -> assertEquals(expectedReward.getRewardTime().getDayOfMonth(), actualReward.getRewardTime().getDayOfMonth()),
                () -> assertEquals(expectedReward.getRewardTime().getHour(), actualReward.getRewardTime().getHour()),
                () -> assertEquals(expectedReward.getProfileId(), actualReward.getProfileId()),
                () -> assertEquals(expectedReward.getPointId(), actualReward.getPointId()),
                () -> assertEquals(expectedReward.getCalcSchemeId(), actualReward.getCalcSchemeId())

        );
        assertEquals(expectedEventStatusMessage, eventStatusMessageCaptor.getValue());
    }

    @Test
    void when_calcRewardFor_with_nullCalcScheme_then_failedEvent() {
        doReturn(null).when(profilePointClient).getCalcSchemeId(any());

        CalcInfoSO calcInfoSO = new CalcInfoSO(UUID.randomUUID().toString(), BigDecimal.valueOf(44.44d), UUID.randomUUID().toString(), UUID.randomUUID().toString());

        rewardService.calcRewardFor(calcInfoSO);

        verify(failedEventProducer).sendMessage(failedCalcInfoMessageCaptor.capture());

        FailedCalcInfo.FailedCalcInfoMessage expectedFailedInfoMessage = FailedCalcInfo.FailedCalcInfoMessage.newBuilder()
                .setCalcInfo(
                        CalcInfo.CalcInfoMessage.newBuilder()
                                .setEventId(calcInfoSO.eventId())
                                .setAmount(BigDecimalUtil.toProtoDecimalValue(calcInfoSO.amount()))
                                .setProfileId(calcInfoSO.profileId())
                                .setPointId(calcInfoSO.pointId())
                                .build())
                .setFailsNum(1)
                .build();

        assertEquals(expectedFailedInfoMessage, failedCalcInfoMessageCaptor.getValue());
    }

    @Test
    void when_calcRewardFor_with_clientException_then_failedEvent() {
        doThrow(new RuntimeException()).when(profilePointClient).getCalcSchemeId(any());

        CalcInfoSO calcInfoSO = new CalcInfoSO(UUID.randomUUID().toString(), BigDecimal.valueOf(44.44d), UUID.randomUUID().toString(), UUID.randomUUID().toString());

        rewardService.calcRewardFor(calcInfoSO);

        verify(failedEventProducer).sendMessage(failedCalcInfoMessageCaptor.capture());

        FailedCalcInfo.FailedCalcInfoMessage expectedFailedInfoMessage = FailedCalcInfo.FailedCalcInfoMessage.newBuilder()
                .setCalcInfo(
                        CalcInfo.CalcInfoMessage.newBuilder()
                                .setEventId(calcInfoSO.eventId())
                                .setAmount(BigDecimalUtil.toProtoDecimalValue(calcInfoSO.amount()))
                                .setProfileId(calcInfoSO.profileId())
                                .setPointId(calcInfoSO.pointId())
                                .build())
                .setFailsNum(1)
                .build();

        assertEquals(expectedFailedInfoMessage, failedCalcInfoMessageCaptor.getValue());
    }

    @Test
    void when_calcRewardFor_with_recalc_then_ok() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        UUID eventId = UUID.randomUUID();

        CalcInfoSO calcInfo = new CalcInfoSO(eventId.toString(), BigDecimal.valueOf(44.44d),
                profileId.toString(), pointId.toString());

        CalcScheme calcScheme = new CalcScheme(calcSchemeId.toString(),
                List.of(new CalcRule(UUID.randomUUID().toString(), 0L, 0.1f, 0), new CalcRule(UUID.randomUUID().toString(), 1500L, 0.15f, 500)), true);

        doReturn(calcSchemeId.toString()).when(profilePointClient).getCalcSchemeId(calcInfo.pointId());
        doReturn(calcScheme).when(calcSchemeClient).getCalcScheme(calcSchemeId.toString());
        doReturn(List.of(new Reward(
                UUID.randomUUID(), BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d),
                BigDecimal.valueOf(1480.747d), "(1480.747 * 0.1 + 0)",
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 3, 4), ZoneOffset.UTC),
                profileId, pointId, calcSchemeId
        ))).when(rewardRepository).getRewardsByPointId(pointId);

        rewardService.calcRewardFor(calcInfo);

        verify(rewardRepository, times(2)).save(rewardCaptor.capture());
        verify(eventStatusProducer, times(2)).sendMessage(eventStatusMessageCaptor.capture());

        Reward expectedReward = new Reward(
                eventId, BigDecimal.valueOf(44.44d), BigDecimal.valueOf(506.666d), BigDecimal.valueOf(1525.187d),
                "(44.44 * 0.15 + 500)", OffsetDateTime.now(), profileId, pointId, calcSchemeId
        );
        EventStatus.EventStatusMessage expectedEventStatusMessage = EventStatus.EventStatusMessage.newBuilder()
                .setEventId(eventId.toString())
                .setEventStatus(EventStatus.Status.PROCESSED)
                .build();

        Reward actualReward = rewardCaptor.getValue();

        assertAll(
                () -> assertEquals(expectedReward.getEventId(), actualReward.getEventId()),
                () -> assertEquals(expectedReward.getEventAmount(), actualReward.getEventAmount()),
                () -> assertEquals(expectedReward.getRewardAmount(), actualReward.getRewardAmount().setScale(3, RoundingMode.HALF_EVEN)),
                () -> assertEquals(expectedReward.getEventAmountTotal(), actualReward.getEventAmountTotal()),
                () -> assertEquals(expectedReward.getFormula(), actualReward.getFormula()),
                () -> assertEquals(expectedReward.getRewardTime().getYear(), actualReward.getRewardTime().getYear()),
                () -> assertEquals(expectedReward.getRewardTime().getMonth(), actualReward.getRewardTime().getMonth()),
                () -> assertEquals(expectedReward.getRewardTime().getDayOfMonth(), actualReward.getRewardTime().getDayOfMonth()),
                () -> assertEquals(expectedReward.getRewardTime().getHour(), actualReward.getRewardTime().getHour()),
                () -> assertEquals(expectedReward.getProfileId(), actualReward.getProfileId()),
                () -> assertEquals(expectedReward.getPointId(), actualReward.getPointId()),
                () -> assertEquals(expectedReward.getCalcSchemeId(), actualReward.getCalcSchemeId())
        );
        assertEquals(expectedEventStatusMessage, eventStatusMessageCaptor.getValue());
    }

    @Test
    void when_findPointMaxReward_then_ok() {
        UUID pointId = UUID.randomUUID();
        YearMonth yearMonth = YearMonth.now();
        OffsetDateTime startOfPeriod = OffsetDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endOfPeriod = OffsetDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), yearMonth.atEndOfMonth().getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

        doReturn(new Reward(
                null, null, BigDecimal.valueOf(506.666d), null,
                null, OffsetDateTime.now(), null, pointId, null
        )).when(rewardRepository).findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountDesc(pointId, startOfPeriod);

        assertEquals(
                new MaxRewardSO(pointId.toString(), BigDecimal.valueOf(506.666d), yearMonth),
                rewardService.findPointMaxReward(pointId.toString(), new PeriodFilterSO(yearMonth, null))
        );

        doReturn(new Reward(
                null, null, BigDecimal.valueOf(6.666d), null,
                null, OffsetDateTime.now(), null, pointId, null
        )).when(rewardRepository).findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountDesc(pointId, endOfPeriod);

        assertEquals(
                new MaxRewardSO(pointId.toString(), BigDecimal.valueOf(6.666d), yearMonth),
                rewardService.findPointMaxReward(pointId.toString(), new PeriodFilterSO(null, yearMonth))
        );

        doReturn(new Reward(
                null, null, BigDecimal.valueOf(0.666d), null,
                null, OffsetDateTime.now(), null, pointId, null
        )).when(rewardRepository).findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountDesc(pointId, startOfPeriod, endOfPeriod);

        assertEquals(
                new MaxRewardSO(pointId.toString(), BigDecimal.valueOf(0.666d), yearMonth),
                rewardService.findPointMaxReward(pointId.toString(), new PeriodFilterSO(yearMonth, yearMonth))
        );
    }

    @Test
    void when_findPointMinReward_then_ok() {
        UUID pointId = UUID.randomUUID();
        YearMonth yearMonth = YearMonth.now();
        OffsetDateTime startOfPeriod = OffsetDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endOfPeriod = OffsetDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), yearMonth.atEndOfMonth().getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC);

        doReturn(new Reward(
                null, null, BigDecimal.valueOf(506.666d), null,
                null, OffsetDateTime.now(), null, pointId, null
        )).when(rewardRepository).findFirstByPointIdAndRewardTimeGreaterThanEqualOrderByRewardAmountAsc(pointId, startOfPeriod);

        assertEquals(
                new MinRewardSO(pointId.toString(), BigDecimal.valueOf(506.666d), yearMonth),
                rewardService.findPointMinReward(pointId.toString(), new PeriodFilterSO(yearMonth, null))
        );

        doReturn(new Reward(
                null, null, BigDecimal.valueOf(6.666d), null,
                null, OffsetDateTime.now(), null, pointId, null
        )).when(rewardRepository).findFirstByPointIdAndRewardTimeLessThanEqualOrderByRewardAmountAsc(pointId, endOfPeriod);

        assertEquals(
                new MinRewardSO(pointId.toString(), BigDecimal.valueOf(6.666d), yearMonth),
                rewardService.findPointMinReward(pointId.toString(), new PeriodFilterSO(null, yearMonth))
        );

        doReturn(new Reward(
                null, null, BigDecimal.valueOf(0.666d), null,
                null, OffsetDateTime.now(), null, pointId, null
        )).when(rewardRepository).findFirstByPointIdAndRewardTimeGreaterThanEqualAndRewardTimeLessThanEqualOrderByRewardAmountAsc(pointId, startOfPeriod, endOfPeriod);

        assertEquals(
                new MinRewardSO(pointId.toString(), BigDecimal.valueOf(0.666d), yearMonth),
                rewardService.findPointMinReward(pointId.toString(), new PeriodFilterSO(yearMonth, yearMonth))
        );
    }

    @Test
    void when_getReward_then_ok() {
        UUID eventId = UUID.randomUUID();
        doReturn(true).when(rewardRepository).existsById(eventId);

        Reward reward = new Reward(
                eventId, BigDecimal.valueOf(6.66d), BigDecimal.valueOf(0.666d), BigDecimal.valueOf(6.66d),
                "(6.66 * 0.1 + 0)", OffsetDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );

        doReturn(reward).when(rewardRepository).getReferenceById(eventId);

        FullRewardSO expectedReward = new FullRewardSO(eventId.toString(), reward.getEventAmount(),
                reward.getRewardAmount(), reward.getRewardTime(), reward.getFormula(), reward.getProfileId().toString(),
                reward.getPointId().toString(), reward.getCalcSchemeId().toString());

        assertEquals(expectedReward, rewardService.getReward(eventId.toString()));
    }

    @Test
    void when_getRewardWithWrongId_then_notFound() {
        doReturn(false).when(rewardRepository).existsById(any());

        assertThrows(HttpStatusCodeException.class, () -> rewardService.getReward(UUID.randomUUID().toString()));
    }

    @Test
    void when_getProfileTotals_with_nullFilter_then_currentTotals() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        doReturn(List.of(pointId.toString())).when(profilePointClient).getPointIdsWithProfileId(profileId.toString());

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d));

        assertEquals(expectedTotals, rewardService.getProfileTotals(profileId.toString(), null));
    }

    @Test
    void when_getProfileTotals_with_periodStartEqualsPeriodEnd_and_equalsCurrentPeriod_then_currentTotals() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        doReturn(List.of(pointId.toString())).when(profilePointClient).getPointIdsWithProfileId(profileId.toString());

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d));

        assertEquals(expectedTotals, rewardService.getProfileTotals(profileId.toString(), new PeriodFilterSO(YearMonth.now(), YearMonth.now())));
    }

    @Test
    void when_getPointTotals_with_nullFilter_then_currentTotals() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d));

        assertEquals(expectedTotals, rewardService.getPointTotals(pointId.toString(), null));
    }

    @Test
    void when_getPointTotals_with_periodStartEqualsPeriodEnd_and_equalsCurrentPeriod_then_currentTotals() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d));

        assertEquals(expectedTotals, rewardService.getPointTotals(pointId.toString(), new PeriodFilterSO(YearMonth.now(), YearMonth.now())));
    }

    @Test
    void when_getPointHistoryTotals_with_nullFilter_then_currentTotals() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        List<HistoryTotalsSO> expectedTotals = List.of(
                new HistoryTotalsSO(BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d), YearMonth.of(2023, 3))
        );

        assertEquals(expectedTotals, rewardService.getPointHistoryTotals(pointId.toString(), null));
    }

    @Test
    void when_getTotals_with_nullFilter_then_currentTotals() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(1727.194d), BigDecimal.valueOf(172.7194d));

        assertEquals(expectedTotals, rewardService.getTotals(null));
    }

    @Test
    void when_getTotals_with_periodStartEqualsPeriodEnd_and_equalsCurrentPeriod_then_currentTotals() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(1727.194d), BigDecimal.valueOf(172.7194d));

        assertEquals(expectedTotals, rewardService.getTotals(new PeriodFilterSO(YearMonth.now(), YearMonth.now())));
    }

    @Test
    void when_getTotals_with_periodStartNotNull_and_periodEndEqualsCurrentPeriod_then_ok() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        YearMonth periodStart = YearMonth.of(2023, 3);

        doReturn(List.of(
                        new RewardHistoryMetrics(
                                pointId,
                                new Reward(UUID.randomUUID(), BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d),
                                        BigDecimal.valueOf(21480.747d), "(1480.747 * 0.1 + 0)",
                                        OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 3, 4), ZoneOffset.UTC),
                                        profileId, pointId, calcSchemeId),
                                BigDecimal.valueOf(5135.41d),
                                YearMonth.of(2023, 4)),
                        new RewardHistoryMetrics(
                                pointId,
                                new Reward(UUID.randomUUID(), BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d),
                                        BigDecimal.valueOf(1480.747d), "(1480.747 * 0.1 + 0)",
                                        OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 3, 4), ZoneOffset.UTC),
                                        profileId, pointId, calcSchemeId),
                                BigDecimal.valueOf(1345.41d),
                                YearMonth.of(2023, 5))
                )
        ).when(rewardHistoryMetricsRepository).getAllByPeriodGreaterThanEqual(periodStart);

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(24688.688d), BigDecimal.valueOf(6653.5394d));

        assertEquals(expectedTotals, rewardService.getTotals(new PeriodFilterSO(periodStart, null)));
    }

    @Test
    void when_getTotals_with_periodEndNotNull_then_ok() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        YearMonth periodEnd = YearMonth.now();

        doReturn(List.of(
                        new RewardHistoryMetrics(
                                pointId,
                                new Reward(UUID.randomUUID(), BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d),
                                        BigDecimal.valueOf(21480.747d), "(1480.747 * 0.1 + 0)",
                                        OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 3, 4), ZoneOffset.UTC),
                                        profileId, pointId, calcSchemeId),
                                BigDecimal.valueOf(5135.41d),
                                YearMonth.of(2023, 4)),
                        new RewardHistoryMetrics(
                                pointId,
                                new Reward(UUID.randomUUID(), BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d),
                                        BigDecimal.valueOf(1480.747d), "(1480.747 * 0.1 + 0)",
                                        OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 3, 4), ZoneOffset.UTC),
                                        profileId, pointId, calcSchemeId),
                                BigDecimal.valueOf(1345.41d),
                                YearMonth.of(2023, 5))
                )
        ).when(rewardHistoryMetricsRepository).getAllByPeriodLessThanEqual(periodEnd);

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(24688.688d), BigDecimal.valueOf(6653.5394d));

        assertEquals(expectedTotals, rewardService.getTotals(new PeriodFilterSO(null, periodEnd)));
    }

    @Test
    void when_getTotals_with_periodStartNotNull_and_periodEndNotNull_then_ok() {
        UUID profileId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        UUID calcSchemeId = UUID.randomUUID();

        mockRewardRepository(profileId, pointId, calcSchemeId);

        rewardService.init();

        YearMonth periodStart = YearMonth.of(2023, 3);
        YearMonth periodEnd = YearMonth.now();

        doReturn(List.of(
                new RewardHistoryMetrics(
                        pointId,
                        new Reward(UUID.randomUUID(), BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d),
                                BigDecimal.valueOf(21480.747d), "(1480.747 * 0.1 + 0)",
                                OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 3, 4), ZoneOffset.UTC),
                                profileId, pointId, calcSchemeId),
                        BigDecimal.valueOf(5135.41d),
                        YearMonth.of(2023, 4)),
                new RewardHistoryMetrics(
                        pointId,
                        new Reward(UUID.randomUUID(), BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d),
                                BigDecimal.valueOf(1480.747d), "(1480.747 * 0.1 + 0)",
                                OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 3, 4), ZoneOffset.UTC),
                                profileId, pointId, calcSchemeId),
                        BigDecimal.valueOf(1345.41d),
                        YearMonth.of(2023, 5))
                )
        ).when(rewardHistoryMetricsRepository).getAllByPeriodGreaterThanEqualAndPeriodLessThanEqual(periodStart, periodEnd);

        TotalsSO expectedTotals = new TotalsSO(BigDecimal.valueOf(24688.688d), BigDecimal.valueOf(6653.5394d));

        assertEquals(expectedTotals, rewardService.getTotals(new PeriodFilterSO(periodStart, periodEnd)));
    }

    private void mockRewardRepository(UUID profileId, UUID pointId, UUID calcSchemeId) {
        YearMonth yearMonth = YearMonth.now();
        OffsetDateTime now = OffsetDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC);

        UUID pointId2 = UUID.randomUUID();
        UUID pointId3 = UUID.randomUUID();

        doReturn(List.of(
                new Reward(
                        UUID.randomUUID(), BigDecimal.valueOf(1480.747d), BigDecimal.valueOf(148.0747d),
                        BigDecimal.valueOf(1480.747d), "(1480.747 * 0.1 + 0)",
                        OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 3, 4), ZoneOffset.UTC),
                        profileId, pointId, calcSchemeId
                ),
                new Reward(
                        UUID.randomUUID(), BigDecimal.valueOf(12.3d), BigDecimal.valueOf(1.23d),
                        BigDecimal.valueOf(12.3d), "(12.3 * 0.1 + 0)",
                        OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 21, 59), ZoneOffset.UTC),
                        UUID.randomUUID(), pointId2, UUID.randomUUID()
                ),
                new Reward(
                        UUID.randomUUID(), BigDecimal.valueOf(234.147d), BigDecimal.valueOf(23.4147d),
                        BigDecimal.valueOf(234.147d), "(234.147 * 0.1 + 0)",
                        OffsetDateTime.of(LocalDateTime.of(2023, 6, 2, 7, 7), ZoneOffset.UTC),
                        UUID.randomUUID(), pointId3, UUID.randomUUID()
                )
        )).when(rewardRepository).findRewardWithMaxEventTotalAndTimestamp(now);
        doReturn(BigDecimal.valueOf(148.0747d)).when(rewardRepository).sumByRewardAndTimestamp(pointId, now);
        doReturn(BigDecimal.valueOf(1.23d)).when(rewardRepository).sumByRewardAndTimestamp(pointId2, now);
        doReturn(BigDecimal.valueOf(23.4147d)).when(rewardRepository).sumByRewardAndTimestamp(pointId3, now);
    }
}
