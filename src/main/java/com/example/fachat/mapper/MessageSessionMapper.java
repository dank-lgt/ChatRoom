package com.example.fachat.mapper;

import com.example.fachat.entity.Friend;
import com.example.fachat.entity.MessageSession;
import com.example.fachat.entity.MessageSessionUserItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageSessionMapper {
    // 1. 根据 userId 获取到该用户都在哪些会话中存在. 返回结果是一组 sessionId
    List<Integer> getSessionIdByUserId(int userId);

    // 2. 根据 sessionId 再来查询这个会话都包含了哪些用户. (刨除最初的自己)
    List<Friend> getFriendsBySessionId(int sessionId,int selfId);

    // 3. 新增一个会话记录, 返回会话的 id
    //    这样的方法返回值 int 表示的是插入操作影响到几行.
    //    此处获取 sessionId 是通过参数的 messageSession 的 sessionId 属性获取的
    int addMessageSession(MessageSession messageSession);

    // 4. 给 message_session_user 表也新增对应的记录
    void addMessageSessionUser(MessageSessionUserItem messageSessionUserItem);

//    获取最新插入的sessionId
//    int getSessionId();
}
