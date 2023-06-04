package ru.itmo.common.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;

import java.time.YearMonth;

public record PeriodFilter(@JsonSerialize(using = YearMonthSerializer.class)
                           @JsonDeserialize(using = YearMonthDeserializer.class)
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM")
                           YearMonth start,
                           @JsonSerialize(using = YearMonthSerializer.class)
                           @JsonDeserialize(using = YearMonthDeserializer.class)
                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM")
                           YearMonth end) {
}
