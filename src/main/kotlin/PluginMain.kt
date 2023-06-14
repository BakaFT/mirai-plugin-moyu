package me.bakaft.plugin;


import command.SearchCommand
import command.SendFriendCommand
import command.SendGroupCommand
import me.bakaft.plugin.event.CustomMessageEvents
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
        CustomMessageEvents.apply(eventChannel)
        
        logger.info { "Moyu Plugin loaded" }

    }


}





