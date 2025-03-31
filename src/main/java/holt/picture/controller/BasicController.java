package holt.picture.controller;


import holt.picture.common.BaseResponse;
import holt.picture.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Weiyang Wu
 * @date 2025/3/30 22:25
 */
@RestController
@RequestMapping("/")
public class BasicController {
    @GetMapping("/test")
    public BaseResponse<String> test() {
        return ResultUtils.success("Hello World");
    }
}
