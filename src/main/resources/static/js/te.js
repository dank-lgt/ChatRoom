////////////////////////////////////////////
// 这里实现标签页的切换
////////////////////////////////////////////

function initSwitchTab() {
    // 1. 先获取到相关的元素(标签页的按钮, 会话列表, 好友列表)
    let tabSession = document.querySelector('.tab .tab-session');
    let tabFriend = document.querySelector('.tab .tab-friend');
    // querySelectorAll 可以同时选中多个元素. 得到的结果是个数组
    // [0] 就是会话列表
    // [1] 就是好友列表
    let lists = document.querySelectorAll('.list');
    // 2. 针对标签页按钮, 注册点击事件.
    //    如果是点击 会话标签按钮, 就把会话标签按钮的背景图片进行设置.
    //    同时把会话列表显示出来, 把好友列表隐藏
    //    如果是点击 好友标签按钮, 就把好友标签按钮的背景图片进行设置.
    //    同时把好友列表显示出来, 把会话列表进行隐藏
    tabSession.onclick = function () {
        // a) 设置图标
        tabSession.style.backgroundImage = 'url(img/对话.png)';
        tabFriend.style.backgroundImage = 'url(img/用户2.png)';
        // b) 让会话列表显示出来, 让好友列表进行隐藏
        lists[0].classList = 'list';
        lists[1].classList = 'list hide';
    }

    tabFriend.onclick = function () {
        // a) 设置图标
        tabSession.style.backgroundImage = 'url(img/对话2.png)';
        tabFriend.style.backgroundImage = 'url(img/用户.png)'
        // b) 让好友列表显示, 让会话列表隐藏
        lists[0].classList = 'list hide';
        lists[1].classList = 'list';
    }
}

initSwitchTab();

function findSessionLi(targetSessionId) {
    // 获取到所有的会话列表中的 li 标签
    let sessionLis = document.querySelectorAll('#session-list li');
    for (let li of sessionLis) {
        let sessionId = li.getAttribute('message-session-id');
        if (sessionId == targetSessionId) {
            return li;
        }
    }
    // 啥时候会触发这个操作, 就比如如果当前新的用户直接给当前用户发送消息, 此时没存在现成的 li 标签
    return null;
}




/////////////////////////////////////////////////////
// 从服务器获取到用户登录数据
/////////////////////////////////////////////////////

function getUserInfo() {
    $.ajax({
        type: 'get',
        url: 'userInfo',
        success: function (res) {
            // 从服务器获取到数据.
            // 校验结果是否有效.
            if (res.code === 200 && res.data.userId > 0) {
                // 如果结果有效, 则把用户名显示到界面上.
                // 同时也可以记录 userId 到 html 标签的属性中. (以备后用)
                let userDiv = document.querySelector('.main .left .user');
                userDiv.innerHTML = res.data.username;
                userDiv.setAttribute("user-id", res.data.userId);
            } else {
                // 如果结果无效, 当前未登录! 则跳转到登录页面.
                alert("当前用户未登录!");
                location.assign('login.html');
            }
        }
    });
}

getUserInfo();

function getFriendList() {
    $.ajax({
        type: 'get',
        url: 'friendList',
        success: function (body) {
            // 1. 先把之前的好友列表的内容, 给清空
            let friendListUL = document.querySelector('#friend-list');
            friendListUL.innerHTML = '';
            // 2. 遍历 body, 把服务器响应的结果, 加回到当前的 friend-list 中.
            for (let friend of body.data) {
                let li = document.createElement('li');
                li.innerHTML = '<h4>' + friend.friendName + '</h4>';
                // 此处把 friendId 也记录下来, 以备后用.
                // 把 friendId 作为一个 html 的自定义属性, 加到 li 标签上就行了.
                li.setAttribute('friend-id', friend.friendId);
                friendListUL.appendChild(li);

                // 每个 li 标签, 就对应界面上的一个好友的选项. 给这个 li 加上点击事件的处理.
                li.onclick = function () {
                    // 参数表示区分了当前用户点击的是哪个好友.
                    clickFriend(friend);
                }
            }
        },
        error: function () {
            console.log('获取好友列表失败!');
        }
    });
}
getFriendList();


// 点击好友列表项, 触发的函数
function clickFriend(friend) {
    var sessionLi = findSessionByName(friend.friendName);
    var sessionListUL = $('ul#session-list');
    if (sessionLi) {
        sessionListUL.prepend(sessionLi);
        sessionLi.click();
    } else {
        //创建会话  并在会话li中填充信息
        sessionLi = $("<li>");
        sessionLi.html('<h3>' + friend.friendName + '</h3>' + '<p></p>');
        //将点击的会话  进行置顶
        sessionListUL.prepend(sessionLi);
        sessionLi.on('click', function () {
            clickSession(sessionLi);
        });
        sessionLi.click();
        createSession(friend.friendId, sessionLi);
    }
    //需要把标签页给切换到 会话列表.
    let tabSession = $('.tab .tab-session');
    tabSession.click();
}

// 判定一下当前这个好友是否有对应的会话
function findSessionByName(username) {
    //在会话列表中找寻符合条件的会话
    var sessionLis = $('#session-list>li');
    for (var i = 0; i < sessionLis.length; i++) {
        var sessionLi = $(sessionLis[i]);
        var h3 = sessionLi.find('h3');
        if (h3.html() == username) {
            return sessionLi;
        }
    }
    return null;
}

// friendId 是构造 HTTP 请求时必备的信息
function createSession(friendId, sessionLi) {
    $.ajax({
        type: 'post',
        url: 'session?toUserId=' + friendId,
        success: function (res) {
            if (res.code == 200 && res.data != null) {
                console.log("会话创建成功! sessionId = " + body.data.sessionId);
                sessionLi.setAttribute('message-session-id', body.data.sessionId);
            }
        },
        error: function () {
            console.log('会话创建失败!');
        }
    })
}

function getSessionList() {
    $.ajax({
        type: 'get',
        url: 'sessionList',
        success: function (res) {
            if (res.code == 200 && res.data.length >= 0) {
                // 1. 清空之前的会话列表
                let sessionListUL = document.querySelector('#session-list');
                sessionListUL.innerHTML = '';
                // 2. 遍历响应的数组, 针对结果来构造页面
                for (let session of res.data) {
                    // 针对 lastMessage 的长度进行截断处理
                    if (session.lastMessage.length > 10) {
                        session.lastMessage = session.lastMessage.substring(0, 10) + '...';
                    }

                    let li = document.createElement('li');
                    // 把会话 id 保存到 li 标签的自定义属性中.
                    li.setAttribute('message-session-id', session.sessionId);
                    li.innerHTML = '<h3>' + session.friends[0].friendName + '</h3>'
                        + '<p>' + session.lastMessage + '</p>';
                    sessionListUL.appendChild(li);

                    // 给 li 标签新增点击事件
                    li.onclick = function () {
                        // 这个写法, 就能保证, 点击哪个 li 标签
                        // 此处对应的 clickSession 函数的参数就能拿到哪个 li 标签.
                        clickSession(li);
                    }
                }
            }
        }
    });
}
getSessionList();



function clickSession(currentLi) {
    // 1. 设置高亮
    var allLis = $('#session-list > li');
    activeSession(allLis, currentLi);
    // 2. 获取指定会话的历史消息 TODO
    var sessionId = $(currentLi).attr("message-session-id");
    getHistoryMessage(sessionId);
}

function activeSession(allLis, currentLi) {
    // 这里的循环遍历, 更主要的目的是取消未被选中的 li 标签的 className
    allLis.each(function () {
        var $li = $(this);
        if ($li.is(currentLi)) {
            $li.addClass('selected');
        } else {
            $li.removeClass('selected');
        }
    });
}

// 这个函数负责获取指定会话的历史消息.
function getHistoryMessage(sessionId) {
    var titleDiv = $('.right .title').html('');
    var messageShowDiv = $('.right .message-show');
    messageShowDiv.html('');
    var selectedH3 = $('#session-list .selected>h3').html();
    if (selectedH3.length > 0) {
        titleDiv.html(selectedH3);
    }
    $.ajax({
        type: 'get',
        url: 'message?sessionId=' + sessionId,
        success: function (res) {
            if (res.code == 200 && res.data.length > 0) {
                for (var message of res.data) {
                    addMessage(messageShowDiv, message);
                }
                // 控制滚动条, 自动滚动到最下方.
                scrollBottom(messageShowDiv);
            }
        }
    })
}

// 把 messageShowDiv 里的内容滚动到底部.
function scrollBottom(elem) {
    // 1. 获取到可视区域的高度
    var scrollHeight = elem[0].scrollHeight;
    var clientHeight = elem.outerHeight();
    // 3. 进行滚动操作, 第一个参数是水平方向滚动的尺寸. 第二个参数是垂直方向滚动的尺寸
    elem.scrollTop(scrollHeight - clientHeight);
}

function addMessage(messageShowDiv, message) {
    $('.right>h3').html('');
    $('.form-group').hide();
    $('.message-input, .ctrl').show();
    $('.message-show').show();
    // 使用这个 div 表示一条消息
    var messageDiv = $('<div></div>');
    var selfUsername = $('.left .user').html();
    if (selfUsername == message.fromName) {
        messageDiv.addClass('message message-right');
    } else {
        messageDiv.addClass('message message-left');
    }
    messageDiv.html('<div class="box">'
        + '<h4>' + message.fromName + '</h4>'
        + '<p>' + message.content + '</p>'
        + '</div>');
    messageShowDiv.append(messageDiv);
}


/////////////////////////////////////////////////////
// 操作 websocket
/////////////////////////////////////////////////////

// 创建 websocket 实例
// let websocket = new WebSocket("ws://127.0.0.1:8080/WebSocketMessage");
var websocket = new WebSocket("ws://" + location.host + "/WebSocketMessage");

websocket.onopen = function () {
    console.log("websocket 连接成功!");
}

websocket.onmessage = function (e) {
    console.log("websocket 收到消息! " + e.data);
    // 此时收到的 e.data 是个 json 字符串, 需要转成 js 对象
    let resp = JSON.parse(e.data);
    if (resp.type == 'message') {
        // 处理消息响应
        handleMessage(resp);
    } else if (resp.type == 'FriendRequest') {
        handleRequest(resp);
    } else {
        // resp 的 type 出错!
        console.log("resp.type 不符合要求!");
    }
}

websocket.onclose = function () {
    console.log("websocket 连接关闭!");
}

websocket.onerror = function () {
    console.log("websocket 连接异常!");
}


function handleRequest(resp) {
    // 创建 li 元素
    var $li = $('<li>', {
        'class': 'fri_req'
    });

    // 创建包裹文本的 div 元素
    var $div = $('<div>');

    // 创建 h3 元素并设置文本内容
    var $h3 = $('<h3>').text(resp.fromName);

    // 创建 h5 元素并设置文本内容
    var $h5 = $('<h5>').text(resp.reason);

    // 将 h3 和 h5 元素添加到 div 元素中
    $div.append($h3, $h5);

    // 创建两个按钮元素
    var $btnAdd = $('<button>', {
        'class': 'add',
        'text': '同意'
    });

    var $btnReject = $('<button>', {
        'class': 'reject',
        'text': '拒绝'
    });
    // 将所有元素添加到 li 元素中
    $li.append($div, $btnAdd, $btnReject);
    let sessionListUL = $('#session-list');
    sessionListUL.prepend($li);
}


function getHistoryFriRequest() {
    $.ajax({
        type: 'get',
        url: 'selectfrireq',
        success: function (res) {
            if (res.code == 200 && res.data.length >0) {
                for (let userData of res.data) {
                    // 创建 li 元素
                    var $li = $('<li>', {
                        'class': 'fri_req'
                    });

                    // 创建包裹文本的 div 元素
                    var $div = $('<div>');

                    // 创建 h3 元素并设置文本内容
                    var $h3 = $('<h3>', {
                        'class': 'sender',
                        'html': userData.username
                    });

                    var $h5 = $('<h5>', {
                        'html': userData.reason
                    });

                    // 将 h3 和 h5 元素添加到 div 元素中
                    $div.append($h3, $h5);

                    // 创建两个按钮元素
                    var $btnAdd = $('<button>', {
                        'class': 'add',
                        'text': '同意'
                    });

                    var $btnReject = $('<button>', {
                        'class': 'reject',
                        'text': '拒绝'
                    });
                    // 将所有元素添加到 li 元素中
                    $li.append($div, $btnAdd, $btnReject);
                    let sessionListUL = $('#session-list');
                    sessionListUL.prepend($li);
                }
            }
        }
    })
}
getHistoryFriRequest();

function handleMessage(resp) {
    var curSessionLi = findSessionLi(resp.sessionId);
    if (curSessionLi == null) {
        // 就需要创建出一个新的 li 标签，表示新会话。
        curSessionLi = $('<li>').attr('message-session-id', resp.sessionId);
        // 此处 p 标签内部应该放消息的预览内容。一会后面统一完成，这里先置空。
        curSessionLi.html('<h3>' + resp.fromName + '</h3>' + '<p></p>');
        // 给这个 li 标签也加上点击事件的处理。
        curSessionLi.on('click', function () {
            clickSession(curSessionLi);
        });
    }
    // 把新的消息, 显示到会话的预览区域 (li 标签里的 p 标签中)
    //    如果消息太长, 就需要进行截断.
    var $p = $(curSessionLi).find('p');
    $p.html(resp.content);
    if ($p.text().length > 10) {
        $p.text($p.text().substring(0, 10) + '...');
    }

    var sessionListUL = $('#session-list');
    sessionListUL.prepend(curSessionLi);
    // 如果当前收到消息的会话处于被选中状态，则把当前的消息给放到右侧消息列表中。
    //    新增消息的同时，注意调整滚动条的位置，保证新消息虽然在底部，但是能够被用户直接看到。
    if (curSessionLi.className == 'selected') {
        // 把消息列表添加一个新消息。
        let messageShowDiv = $('.right .message-show');
        addMessage(messageShowDiv, resp);
        scrollBottom(messageShowDiv);
    }
}


/////////////////////////////////////////////////////
// 实现消息发送/接收逻辑
/////////////////////////////////////////////////////

function initSendButton() {
    var sendButton = $('.right .ctrl button');
    var messageInput = $('.right .message-input');
    sendButton.on('click', function () {
        sendMessage(messageInput);
    });

    messageInput.on('keypress', function (event) {
        if (event.which === 13) { // 按下 Enter 键
            sendMessage(messageInput);
            event.preventDefault(); // 阻止默认的 Enter 键行为（例如换行）
        }
    });
}


function sendMessage(messageInput) {
    // 输入框不存在内容
    if (!messageInput.val()) {
        return;
    }
    var selectedLi = $('#session-list .selected');
    // 未被选中的会话
    if (selectedLi.length === 0) {
        return;
    }
    var sessionId = selectedLi.attr('message-session-id');
    var req = {
        type: 'message',
        sessionId: sessionId,
        content: messageInput.val()
    };
    req = JSON.stringify(req);
    console.log("[websocket] send: " + req);
    // 通过 websocket 发送消息
    websocket.send(req);
    // 发送完成之后，清空输入框
    messageInput.val('');
}

initSendButton();

function friendRequest() {
    $('.right>h3').html('');
    $('.form-group').html('')
    let search = $('#search-msg');
    if (!search.val()) {
        return;
    }
    let searchbtn = $('#search-btn');
    $('.message-show').hide();
    $('.message-input, .ctrl').hide();
    // searchbtn.on('click', function () {
    $.ajax({
        type: 'post',
        url: 'friendrequest',
        data: {
            "search": search.val()
        },
        success: function (res) {
            if (res.code == 200 && res.data.length > 0) {
                // console.log(res.data);
                for (let user of res.data) {
                    let $formGroup = $('<div>', {
                        'class': 'form-group'
                    });
                    let $label = $('<span>', {
                        'for': 'friendName',
                        'text': user.username // 使用用户的名字作为标签文本
                    });

                    let $textarea = $('<textarea>', {
                        'class': 'reason',
                        'name': 'reason',
                        'rows': '1',
                        'placeholder': '发送好友请求'
                    });

                    let $button = $('<button>', {
                        'class': 'addFriendBtn',
                        'text': '添加好友',
                        'data-user-id': user.userId
                    });
                    $('.title').html('好友搜索结果');
                    // 将元素添加到父元素中
                    $formGroup.append($label, $textarea, $button);
                    // 将元素插入到类名为 right 的父元素中的标题后面
                    $formGroup.insertAfter('.right .title');
                }
                $('.addFriendBtn').on('click', function () {
                    let userId = $(this).data('user-id');
                    let reason = $(this).siblings('.reason');
                    sendFriendReq(userId, reason);
                });
                search.val('');
            } else {
                $('.title').html('好友搜索结果');
                let h3 = $('<h3>' + res.msg + '</h3>');
                h3.insertAfter('.right .title');
            }
        }
    })
    // })
}

//点击添加按钮 发送好友请求
function sendFriendReq(userId, reason) {
    console.log(userId);
    console.log(reason.val());
    if (reason.val() == null || reason.val() == '') {
        alert("您的好友请求为空，不能发送！")
        return;
    }
    if (userId == null) {
        return;
    }
    var req = {
        type: 'FriendRequest',
        friendId: userId,
        reason: reason.val()
    };
    req = JSON.stringify(req);
    console.log("[websocket] send: " + req);
    // 通过 websocket 发送消息
    websocket.send(req);
    //提示好友请求发送完成
    reason.val('');
    alert('好友请求发送成功！');
}

function addriReq() {
    // let addFriReqBtn = $('.add');
    // let rejectBtn = $('.reject');
    // if (add == null) {
    //     return;
    // }
    // if (rejectBtn == null) {
    //     return;
    // }
    let senderName = $('.sender').html();
    $.ajax({
        type: 'post',
        url: 'addFriendRequest',
        data: {
            "senderName": senderName
        },
        success: function (res) {
            if (res.code == 200 && res.data == 1) {
                var $li = $(this).closest('li.fri_req');
                // 删除对应的 li 元素
                $li.remove();
                alert('添加成功！');
                getFriendList();
            }
        }
    })
}