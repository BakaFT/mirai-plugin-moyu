package command;

import me.bakaft.plugin.PluginMain
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.ContactUtils.getFriendOrGroup
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

object SendCommand: RawCommand(
    PluginMain,
    "send",
    usage = "send <Contact Id> <Message>",
    description = "向指定联系人发送一条消息",
    prefixOptional = true,
){
    var LastSentContactId:Long? = null

    @OptIn(ConsoleExperimentalApi::class)
    override suspend fun CommandContext.onCommand(args: MessageChain){
        val botInstance = Bot.instances[0]
        if (args.size != 2){
            println(usage)
            return
        }

        val contactIdOrDash = args[0].content
        val message = args[1]

        // 如果是-，则发送给 上一次发送 的目标
        if (contactIdOrDash.equals("-")){
            if(LastSentContactId == null){
                println("你还没发送过消息，LastSentContactId不存在")
                return
            }
            botInstance.getFriendOrGroup(LastSentContactId!!).sendMessage(message)
            return
        }
        // 否则发送给指定的人
        val contactId = contactIdOrDash.toLong()
        botInstance.getFriendOrGroup(contactId).sendMessage(message)
        LastSentContactId = contactId
    }
}

