package com.example.fachat.mapper;

import com.example.fachat.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //获取会话最后一条消息
    String getLastMessageBySessionId(int sessionId);

    // 获取指定会话历史消息列表
    // 有的会话里, 历史消息可能特别特别多.
    // 此处做出一个限制, 默认只取最近的 100 条消息.
    List<Message> getMessageBySessionId(int sessionId,int limit);

    void add(Message message);
    //更新消息读取状态
    void updateMessageStatus(int sessionId);

    //统计未读会话消息的数量
    int MessageUnRead(int sessionId, int fromId);
}
