package org.gdsccau.team5.safebridge.common.code;

import java.util.Arrays;
import java.util.List;
import org.gdsccau.team5.safebridge.common.code.success.CommonSuccessCode;

public class SuccessCodeResolver {

    private static final List<Class<? extends SuccessCode>> SUCCESS_CODE_CLASSES = List.of(
            CommonSuccessCode.class
    );

    public static SuccessCode fromCodeName(String successCodeName) {
        return SUCCESS_CODE_CLASSES.stream()
                .flatMap(enumClass -> Arrays.stream(enumClass.getEnumConstants())) // 각 enum 클래스의 상수를 순회
                .filter(code -> code.getName().equals(successCodeName)) // name()을 이용하여 상수 이름을 비교
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 SuccessCode가 없습니다 : " + successCodeName));
    }
}
