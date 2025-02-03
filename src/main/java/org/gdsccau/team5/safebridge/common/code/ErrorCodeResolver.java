package org.gdsccau.team5.safebridge.common.code;

import java.util.Arrays;
import java.util.List;
import org.gdsccau.team5.safebridge.common.code.error.CommonErrorCode;

public class ErrorCodeResolver {

    private static final List<Class<? extends ErrorCode>> ERROR_CODE_CLASSES = List.of(
            CommonErrorCode.class
    );

    public static ErrorCode fromCodeName(String errorCodeName) {
        return ERROR_CODE_CLASSES.stream()
                .flatMap(enumClass -> Arrays.stream(enumClass.getEnumConstants())) // 각 enum 클래스의 상수를 순회
                .filter(code -> code.getName().equals(errorCodeName)) // name()을 이용하여 상수 이름을 비교
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 ErrorCode가 없습니다 : " + errorCodeName));
    }
}