package ru.itmo.common.service.util;

import com.google.protobuf.ByteString;
import lombok.experimental.UtilityClass;
import ru.itmo.common.domain.message.ProtoBigDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

@UtilityClass
public class BigDecimalUtil {
    public static BigDecimal toJavaBigDecimal(ProtoBigDecimal.DecimalValue protoDecimalValue) {
        return new BigDecimal(
                new BigInteger(protoDecimalValue.getValue().toByteArray()),
                protoDecimalValue.getScale(),
                new MathContext(protoDecimalValue.getPrecision())
        );
    }

    public static ProtoBigDecimal.DecimalValue toProtoDecimalValue(BigDecimal javaBigDecimal) {
        return ProtoBigDecimal.DecimalValue.newBuilder()
                .setScale(javaBigDecimal.scale())
                .setPrecision(javaBigDecimal.precision())
                .setValue(ByteString.copyFrom(javaBigDecimal.unscaledValue().toByteArray()))
                .build();
    }
}
