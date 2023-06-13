# mirai-plugin-moyu

这是一个摸鱼插件，可以让你将Mirai Console作为一个丐版的QQ客户端使用

# 命令介绍

不加额外说明的情况下，所有命令都可以通过`/command`或`command`的方式调用

## /send

`/send <Contact ID> <Message>`

向指定对象发送文字消息

其中`Contact ID`是QQ号或QQ群号，`Message`为纯文本消息

特别地，当`Contact ID`为`-`时，表示向`上次发送的对象`发送消息，不用再输入ID

## /search

`/search <neddle>`

搜索名字中包含`neddle`的好友或者群

目前的实现需要完善，只能返回一个







