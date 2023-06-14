package me.bakaft.plugin;


import command.SearchCommand
import command.SendFriendCommand
import command.SendGroupCommand
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.AnonymousMember
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "me.bakaft.moyu",
        name = "摸鱼插件",
        version = "0.1.0"
    ) {
        author("bakaft@outlook.com")
        info(
            """
            这是一个摸鱼插件，
            你可以在Mirai Console中进行简单的消息发送操作,
            添加了: /send,/search 指令
        """.trimIndent()
        )
    }
) {
    override fun onEnable() {
        // Commands
        CommandManager.registerCommand(SendGroupCommand)
        CommandManager.registerCommand(SendFriendCommand)
        CommandManager.registerCommand(SearchCommand)

        // EventHandlers
        val eventChannel = GlobalEventChannel.parentScope(this)
        // Console主动发送
        eventChannel.subscribeAlways<GroupMessagePostSendEvent> {
            println("[群][${target.name}(${target.id})] <- $message")
        }
        eventChannel.subscribeAlways<FriendMessagePostSendEvent> {
            println("[好友][${target.remarkOrNick}(${target.id})] <- $message")
        }

        // Console 被动接受
        eventChannel.subscribeAlways<GroupMessageEvent> {
            val displayId = if (sender is AnonymousMember) "匿名" else sender.id.toString()
            println("[群][${group.name}(${group.id})] $senderName($displayId) -> ${message.content}")
        }
        eventChannel.subscribeAlways<FriendMessageEvent> {
            println("[好友][${sender.remarkOrNick}(${sender.id})] -> ${message.content}")
        }

        // 同步机器人在其他客户端的发送
        eventChannel.subscribeAlways<GroupMessageSyncEvent> {
            println("[群][${group.name}(${group.id})][SYNC] <- ${message.content}")
        }
        eventChannel.subscribeAlways<FriendMessageSyncEvent> {
            println("[好友][${sender.remarkOrNick}(${sender.id})][SYNC] <- ${message.content}")
        }
        logger.info { "Moyu Plugin loaded" }

    }


}





