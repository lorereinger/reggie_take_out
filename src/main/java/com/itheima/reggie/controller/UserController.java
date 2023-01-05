package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.Utils.SMSUtils;
import com.itheima.reggie.Utils.ValidateCodeUtils;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 短信验证码登录
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        log.info("发送短信验证码：{}",user);
        //获取手机号
        String phoneNumbers = user.getPhone();

        //手机号不为空
        if(StringUtils.isNotEmpty(phoneNumbers)){
            //生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("生成的验证码为：{}",code);
            //将验证码存入session
            //session.setAttribute("code",code);

            //将生成的验证码保存到redis中，并设置有效期
            redisTemplate.opsForValue().set(phoneNumbers,code,5, TimeUnit.MINUTES);
            //发送短信
            //SMSUtils.sendMessage("大仙宝藏个人公众号","1605265",phoneNumbers,code);
            return R.success("手机短信验证码发送成功");
        }
        return R.error("发送失败");
    }

    /**
     * 用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info("登录：{}",map.toString());
        //获取手机号
        String phone = (String) map.get("phone");
        //获取验证码
        String code = (String) map.get("code");
        //获取session中的验证码
        //String sessionCode = (String) session.getAttribute("code");
        //从redis中获取缓存的验证码
        String sessionCode = (String)redisTemplate.opsForValue().get(phone);
        //判断验证码是否正确
        if(code.equals(sessionCode)){
            //验证码正确，登录成功
            //判断当前用户是否为新用户，如果是新用户自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User one = userService.getOne(queryWrapper);
            if (one == null) {
                //新用户，自动注册
                one = new User();
                one.setPhone(phone);
                one.setStatus(1);
                userService.save(one);
            }
            session.setAttribute("user",one.getId());
            //如果用户登陆成功，删除缓存的验证码
            redisTemplate.delete(phone);
            return R.success(one);
        }
        return R.error("登录失败");
    }


    //用户登出
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        //清理Session中保存的当前用户登录的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}

