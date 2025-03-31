package holt.picture.controller;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Weiyang Wu
 * @date 2025/3/30 22:25
 */
@RestController
@MapperScan("holt.picture.mapper")
public class BasicController {
    @RequestMapping("/user")
    @ResponseBody
    public String user() {
        return "user";
    }
}
