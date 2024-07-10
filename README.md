@[toc]
# 网页聊天室自动化测试&性能测试

## 一、聊天室页面

登录页面

![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/ec7292402dac484998674ce35a9dae9e.png#pic_center)


注册页面
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/80ba433075c94b0da329e52d10b5130f.png#pic_center)
主页面

![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/c171faf3e11d4ca48322cedf1bd22777.png#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/26d118062c2f4b3996059e96a839e847.png#pic_center)
好友搜索页面

![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/04794e4ee5d14b3a95e8de68d2ad23bd.png#pic_center)



## 二、聊天室测试用例



## 三、自动化测试
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/137d2059118a4d42b078365d90fd1d5f.png#pic_center)
### 登录测试

成功用例
```
 @ParameterizedTest
    @CsvFileSource(resources = "LoginSuccess.csv")
    @Order(1)
    void LoginSuccess(String userName,String userPassword,String client_url) throws InterruptedException {
        // 1. 打开聊天室登录页
         edgeDriver.get("http://192.168.10.19:8083/login.html");
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // 2. 输入用户名
        WebElement userNameInput =  edgeDriver.findElement(By.cssSelector("#username"));
        userNameInput.sendKeys(userName);
        WebElement passwordInput =  edgeDriver.findElement(By.cssSelector("#password"));
        passwordInput.sendKeys(userPassword);
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // 3. 点击登录按钮
         edgeDriver.findElement(By.cssSelector("#submit")).click();
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        sleep(1000);
        // 忽略弹窗
         edgeDriver.switchTo().alert().dismiss();
        // 4. 页面跳转
        // 5. 判断url是否一致
        String url =  edgeDriver.getCurrentUrl();
        Assertions.assertEquals(client_url, url);
        sleep(1000);
        // 6. 判断展示的用户名是否一致
        String cur_name =  edgeDriver.findElement(By.cssSelector("body > div.client-container > div > div.left > div.user")).getText();
        Assertions.assertEquals(userName, cur_name);
   }
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/bc712c37c3ca42c9bcd62aee7cfa6fbb.png#pic_center)
失败用例

```
@ParameterizedTest
@CsvFileSource(resources = "LoginFailed.csv")
@Disabled
void LoginFailed(String userName,String userPassword,String LoginUrl) throws InterruptedException {
        // 1. 打开聊天室登录页
         edgeDriver.get("http://192.168.10.19:8083/login.html");
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // 2. 输入用户名
        WebElement userNameInput =  edgeDriver.findElement(By.cssSelector("#username"));
        userNameInput.sendKeys(userName);
        WebElement passwordInput =  edgeDriver.findElement(By.cssSelector("#password"));
        passwordInput.sendKeys(userPassword);
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // 3. 点击登录按钮
         edgeDriver.findElement(By.cssSelector("#submit")).click();
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        sleep(2000);
        // 忽略弹窗
         edgeDriver.switchTo().alert().dismiss();
        // 4. 页面跳转
        // 5. 判断url是否一致
        String url =  edgeDriver.getCurrentUrl();
        Assertions.assertEquals(LoginUrl, url);
        
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/63a16ec6852148f39a70e4257af5deb4.png#pic_center)
### 注册测试

成功用例

```
@ParameterizedTest
@CsvFileSource(resources = "RegisterSuccess.csv")
@Disabled
void RegisterSuccess (String username,String password,String url) throws InterruptedException {
    // 1. 打开注册页面
     edgeDriver.get("http://192.168.10.19:8083/register.html");
     edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    // 2. 输入用户名和密码
     edgeDriver.findElement(By.cssSelector("#username")).sendKeys(username);
     edgeDriver.findElement(By.cssSelector("#password")).sendKeys(password);
    //3. 点击注册按钮
     edgeDriver.findElement(By.cssSelector("#submit")).click();
    sleep(1000);
    // 忽略弹窗
     edgeDriver.switchTo().alert().accept();
     edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    // 4. 页面跳转
    // 5. 判断url是否一致
    String cur_url =  edgeDriver.getCurrentUrl();
    Assertions.assertEquals(url, cur_url);
}
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/ca48456edff04793afe2b7a48835f451.png#pic_center)

失败用例

```
@ParameterizedTest
    @CsvFileSource(resources = "RegisterFailed.csv")
    @Disabled
    void RegisterFailed (String username,String password,String url) throws InterruptedException {
        // 1. 打开注册页面
         edgeDriver.get("http://192.168.10.19:8083/register.html");
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        // 2. 输入用户名和密码
         edgeDriver.findElement(By.cssSelector("#username")).sendKeys(username);
         edgeDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        //3. 点击注册按钮
         edgeDriver.findElement(By.cssSelector("#submit")).click();
        sleep(1000);
        // 忽略弹窗
         edgeDriver.switchTo().alert().accept();
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        // 4. 页面跳转
        // 5. 判断url是否不一致
        String cur_url =  edgeDriver.getCurrentUrl();
        Assertions.assertNotEquals(url, cur_url);
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/bbf70ccdd36a40e2bffc85d7ff673a93.png#pic_center)
### 主界面测试

```
@Test
    @Order(5)
    void ClickFriend() {
        // 1. 打开主界面
         edgeDriver.get("http://192.168.10.19:8083/client.html");
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        // 2. 点击好友按钮
         edgeDriver.findElement(By.cssSelector("body > div.client-container > div > div.left > div.tab > div.tab-friend")).click();

         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        // 4. 获取好友名
        // 5. 断言好友名是否一致
        String friendname =  edgeDriver.findElement(By.cssSelector("#friend-list > li:nth-child(1) > h4")).getText();
        Assertions.assertEquals("lisi", friendname);
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/0a9a38e76b1f46728c86cc4cc4e5d68a.png#pic_center)
### 消息测试

```
@ParameterizedTest
    @CsvFileSource(resources = "SendMessage.csv")
    @Order(6)
    void SendMessage(String message) throws InterruptedException {
         edgeDriver.get("http://192.168.10.19:8083/client.html");
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        // 选中和赵六的会话并点击
        WebElement element1 =  edgeDriver.findElement(By.cssSelector("body > div.client-container"));
        WebElement element2 = element1.findElement(By.cssSelector(".main"));
        WebElement element3 = element2.findElement(By.cssSelector(".left"));
        WebElement element4 = element3.findElement(By.cssSelector(".list"));
        WebElement element5 = element4.findElement(By.cssSelector("li"));
        WebElement element6 = element5.findElement(By.cssSelector("h3"));
//        element6.click();
        WebElement element = element1.findElement(By.cssSelector("#session-list > li:nth-child(1)"));
        element.click();
        // 输入消息
         edgeDriver.findElement(By.cssSelector("body > div.client-container > div > div.right > textarea")).sendKeys(message);
        //点击发送按钮
         edgeDriver.findElement(By.cssSelector("body > div.client-container > div > div.right > div.ctrl > button")).click();
        sleep(2000);
        // 判断消息是否发送成功
        String cur_message =  edgeDriver.findElement(By.cssSelector("body > div.client-container > div > div.right > div.message-show > div:last-child > div > p")).getText();
        Assertions.assertNotEquals("", cur_message);
    }
    /*
    * 接收消息
    * */
    @Test
    @Order(7)
    void ReceiveMessage() throws InterruptedException {
        //登录
        // 1. 打开聊天室登录页
         edgeDriver.get("http://192.168.10.19:8083/login.html");
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // 2. 输入用户名zhaoliu信息
        WebElement userNameInput =  edgeDriver.findElement(By.cssSelector("#username"));
        userNameInput.sendKeys("zhaoliu");
        WebElement passwordInput =  edgeDriver.findElement(By.cssSelector("#password"));
        passwordInput.sendKeys("123");
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // 3. 点击登录按钮
         edgeDriver.findElement(By.cssSelector("#submit")).click();
         edgeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        sleep(1000);
        // 忽略弹窗
         edgeDriver.switchTo().alert().dismiss();
        // 4. 页面跳转
        // 5. 判断url是否一致
        String url =  edgeDriver.getCurrentUrl();
        Assertions.assertEquals("http://60.204.223.152:8080/client.html", url);
        sleep(1000);
        // 6. 判断展示的用户名是否一致
        String cur_name =  edgeDriver.findElement(By.cssSelector("body > div.client-container > div > div.left > div.user")).getText();
        Assertions.assertEquals("zhaoliu", cur_name);

        // 校验
        // 点击和zhangsan的会话
        WebElement element1 =  edgeDriver.findElement(By.cssSelector("body > div.client-container"));
        WebElement element2 = element1.findElement(By.cssSelector(".main"));
        WebElement element3 = element2.findElement(By.cssSelector(".left"));
        WebElement element4 = element3.findElement(By.cssSelector(".list"));
        WebElement element5 = element4.findElement(By.cssSelector("li"));
        WebElement element6 = element5.findElement(By.cssSelector("h3"));
        element6.click();

        // 获取最新消息
        WebElement lastMessage =  edgeDriver.findElement(By.cssSelector("body > div.client-container > div > div.right > div.message-show > div:last-child > div > p"));
        // 获取好友名
        WebElement friendName =  edgeDriver.findElement(By.cssSelector("body > div.client-container > div > div.right > div.message-show > div:last-child > div > h4"));
        String cur_message = lastMessage.getText();
        String cur_friendName = friendName.getText();
        // 判断好友名是否一致
        Assertions.assertEquals("zhangsan", cur_friendName);
        // 判断消息是否一致
        Assertions.assertNotEquals("", cur_message);
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/942d2294adb344b584af5d81190cfd8f.png#pic_center)

> 注意：这里出现的不同报错是因为网络波动导致消息发送失败

### 性能测试

测试脚本

```
Action()
{
	web_url("login.html", 
		"URL=http://192.168.10.19:8083/login.html", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=", 
		"Snapshot=t10.inf", 
		"Mode=HTML", 
		EXTRARES, 
		"Url=/img/wx.jpg", ENDITEM, 
		LAST);

	lr_think_time(4);

	web_submit_data("login", 
		"Action=http://192.168.10.19:8083/login", 
		"Method=POST", 
		"RecContentType=application/json", 
		"Referer=http://192.168.10.19:8083/login.html", 
		"Snapshot=t16.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=username", "Value=zhansan", ENDITEM, 
		"Name=password", "Value=123", ENDITEM, 
		"Name=captcha", "Value=qb3y", ENDITEM, 
		LAST);

	web_url("client.html", 
		"URL=http://192.168.10.19:8083/client.html", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=http://192.168.10.19:8083/login.html", 
		"Snapshot=t18.inf", 
		"Mode=HTML", 
		EXTRARES, 
		"Url=/img/%E6%90%9C%E7%B4%A2.png", "Referer=http://192.168.10.19:8083/css/client.css", ENDITEM, 
		"Url=/img/%E5%AF%B9%E8%AF%9D.png", "Referer=http://192.168.10.19:8083/css/client.css", ENDITEM, 
		"Url=/img/%E7%94%A8%E6%88%B72.png", "Referer=http://192.168.10.19:8083/css/client.css", ENDITEM, 
		"Url=/img/view.jpg", "Referer=http://192.168.10.19:8083/css/client.css", ENDITEM, 
		"Url=/userInfo", ENDITEM, 
		"Url=/friendList", ENDITEM, 
		"Url=/selectfrireq", ENDITEM, 
		"Url=/img/%E5%A4%95%E9%98%B3.jpg", ENDITEM, 
		"Url=/sessionList", ENDITEM, 
		"Url=/img/%E5%96%9C%E6%9E%81%E8%80%8C%E6%B3%A3.jpg", ENDITEM, 
		"Url=/message?sessionId=13&limit=100", ENDITEM, 
		LAST);




	web_custom_request("getavatar", 
		"URL=http://192.168.10.19:8083/getavatar", 
		"Method=POST", 
		"Resource=0", 
		"RecContentType=application/json", 
		"Referer=http://192.168.10.19:8083/client.html", 
		"Snapshot=t22.inf", 
		"Mode=HTML", 
		"EncType=", 
		EXTRARES, 
		"Url=http://t.wg.360-api.cn/ap/bar/pull?mid=&cver=", "Referer=", ENDITEM, 
		LAST);

	lr_think_time(7);

	web_submit_data("friendrequest", 
		"Action=http://192.168.10.19:8083/friendrequest", 
		"Method=POST", 
		"RecContentType=application/json", 
		"Referer=http://192.168.10.19:8083/client.html", 
		"Snapshot=t23.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=search", "Value=ad", ENDITEM, 
		LAST);

	web_url("login.html_2", 
		"URL=http://192.168.10.19:8083/login.html", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=", 
		"Snapshot=t24.inf", 
		"Mode=HTML", 
		EXTRARES, 
		"Url=/layui/font/iconfont.woff2?v=282", "Referer=http://192.168.10.19:8083/layui/css/layui.css", ENDITEM, 
		LAST);

	web_websocket_close("ID=0", 
		"Code=1001", 
		LAST);
	return 0;
}
```



模拟多用户同时登录

![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/706fddf001a9409382b0cc383a6f1dd0.png#pic_center)

- 在线用户数

![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/fe337620fd034525b4afcbdbc6a0a303.png#pic_center)

- 点击率
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/9a37f2dd89004d49abc4e3ac55910e1b.png#pic_center)



- 吞吐率

  ![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/3518197207854371b2e23ec98df4f396.png#pic_center)

- 每秒连接数

  ![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/e84a73ac09044990a53ddb6ab6ad8418.png#pic_center)

- 每秒HTTP响应

  ![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/009c7998b90e46868490d1933805c31f.png#pic_center)

###  测试分析结果
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/4cadd03b7f2e47f88514d6d4b29e7ceb.png#pic_center)
## 总结

至此就是我对网页聊天室所做的测试结果，其中如果有什么不完善的地方也欢迎指出.