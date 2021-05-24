package com.szu.common.protocol;
/*
 * @Author 郭学胤
 * @University 深圳大学
 * @Description
 * @Date 2021/5/23 23:54
 */

public class SZUMessage {

    Head head;
    Content content;

    public SZUMessage(Head head, Content content) {
        this.head = head;
        this.content = content;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
