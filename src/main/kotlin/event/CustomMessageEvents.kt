package me.bakaft.plugin.event

import net.mamoe.mirai.contact.AnonymousMember
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.content

object CustomMessageEvents {
    fun apply(eventChannel: EventChannel<*>){
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
    }

}