package com.example.fachat.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.example.fachat.common.AjaxResult;
import com.example.fachat.common.ApplicationVariable;
import com.example.fachat.common.PasswordTool;
import com.example.fachat.config.UserSessionTools;
import com.example.fachat.entity.User;
import com.example.fachat.mapper.UserMapper;
import com.example.fachat.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/login")
    @ResponseBody
    public AjaxResult login(String username, String password,String captcha, HttpServletRequest req) {
        User user = userService.login(username);
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return AjaxResult.fail(-2, "用户名或者密码有误！");
        }
        if (user == null || user.getUserId() < 0) {
            return AjaxResult.fail(-2, "用户名或者密码有误！");
        }
        if (captcha == null )
            return  AjaxResult.fail(-2,"用户名或者密码有误！");
        if (!PasswordTool.decrypt(password, user.getPassword()))
            return AjaxResult.fail(-2, "用户名或者密码有误！");
        HttpSession session = req.getSession();
        if (captcha.equalsIgnoreCase((String) session.getAttribute(ApplicationVariable.SESSION_KEY_CHECK_CODE))) {
            //将当前成功登录的用户存储到session
            session.setAttribute(ApplicationVariable.SESSION_KEY_USERINFO, user);
            return AjaxResult.success(1);
        }
        return null;
    }


    @RequestMapping("/register")
    @ResponseBody
    public AjaxResult reg(User user) {
//        if (user == null || !StringUtils.hasLength(user.getUsername()) ||
//                !StringUtils.hasLength(user.getPassword()))
//            return AjaxResult.fail(-1, "参数有误！");
//        user.setPassword(PasswordTool.encrypt(user.getPassword()));
//        int result = userService.reg(user);
//        return AjaxResult.success(result);
        try {
            if (user == null || !StringUtils.hasLength(user.getUsername()) ||
                    !StringUtils.hasLength(user.getPassword())) {
                return AjaxResult.fail(-1, "参数有误！");
            }
            if (userService.login(user.getUsername()) != null){
                return AjaxResult.fail(-1, "用户已注册！");
            }
            user.setPassword(PasswordTool.encrypt(user.getPassword()));
            int res = userService.reg(user);
            return AjaxResult.success(res);
        }catch (DuplicateKeyException e){
            //如注册失败 insert 抛出异常 说明名字重复
            user = new User();
            return AjaxResult.fail(-2,"参数有误！");
        }
    }

    @RequestMapping("/userInfo")
    @ResponseBody
    public AjaxResult getUserInfo(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session == null){
            return AjaxResult.fail(-1,"用户会话不存在");
        }
        User user = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return AjaxResult.fail(-2,"当前用户不存在");
        }
        user.setPassword("");
        return AjaxResult.success(user);
    }

    @RequestMapping("/friendrequest")
    @ResponseBody
    public AjaxResult FriendRequest(String search,HttpServletRequest request){
        HttpSession session = request.getSession();
        if (session == null){
            return AjaxResult.fail(-1,"用户错误！");
        }
        User user = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return AjaxResult.fail(-2,"当前用户不存在");
        }
        if (search == null || search == ""){
            return AjaxResult.fail(-1, "此参数为空！");
        }
        List<User> users = userService.searchUser(search, user.getUserId());
        if (users.isEmpty()){
            return AjaxResult.fail(-1,"搜索不到对象！");
        }
        return AjaxResult.success(users);
    }

    @RequestMapping("/getCode")
    public void getCode(HttpServletResponse response,HttpServletRequest request) {
        HttpSession session = request.getSession();
        // 随机生成 4 位验证码
        RandomGenerator randomGenerator = new RandomGenerator(4);
        // 定义图片的显示大小
         LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40);
        response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");
        try {
            // 调用父类的 setGenerator() 方法，设置验证码的类型
            lineCaptcha.setGenerator(randomGenerator);
            // 输出到页面
            lineCaptcha.write(response.getOutputStream());
            session.setAttribute(ApplicationVariable.SESSION_KEY_CHECK_CODE,lineCaptcha.getCode());
            // 打印日志
            logger.info("生成的图片验证码:{}", lineCaptcha.getCode());
            // 关闭流
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping ("/upload-avatar")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(HttpServletRequest request,@RequestParam("avatar") MultipartFile file) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请选择文件.");
        }
        try {
            // 保存文件到服务器的示例路径，你可以根据实际情况修改
            byte[] bytes = file.getBytes();
            Path absololutePath = Paths.get(ApplicationVariable.IMG_PATH_ABSOLUTE + file.getOriginalFilename());
            String photoPathRelative = ApplicationVariable.IMG_PATH_RELATIVE+file.getOriginalFilename();
            int res = userService.updatePhotoById(user.getUserId(),photoPathRelative);
            Files.write(absololutePath, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("文件上传失败.");
        }
        return ResponseEntity.ok("文件上传成功.");
    }


    @RequestMapping("/getavatar")
    @ResponseBody
    public AjaxResult getMyAvatar(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO);
        if (user == null){
            return AjaxResult.fail(-1,"参数错误！");
        }
        User user1 = userService.getAvatarByUseId(user.getUserId());
        if (user1 == null) {
            return AjaxResult.fail(-1, "无法获取用户头像！");
        }
        // 更新用户会话中的头像相对路径
        user.setAvatar(user1.getAvatar());
        UserSessionTools.updateLoginUser(request,user);
        return AjaxResult.success(user);
    }
}
