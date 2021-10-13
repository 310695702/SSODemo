package top.hyx.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 9:55
 **/
@AllArgsConstructor
@Getter
public class MyException extends Exception{
    private final Integer code;
    private final String message;

}
