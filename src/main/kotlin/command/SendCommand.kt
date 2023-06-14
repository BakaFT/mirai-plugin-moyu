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
    "send","s",
    usage = "send <Contact Id> <Message>",
    description = "向指定联系人发送一条消息",
    prefixOptional = true,
){
    var LastSentContactId:Long? = null

    @OptIn(ConsoleExperimentalApi::class)
    override suspend fun CommandContext.onCommand(args: MessageChain){
        val botInstance = Bot.instances[0]

        if (args.size <= 1){
            println(usage)
            return
        }

        val contactIdOrDash = args[0].content
        // Message can be split by space, so we need to join them from args[1] to args[args.size-1]
        // Such as args[1] = "I",args[2]="AM",args[3]="JOHN", we need to join them to "I AM JOHN"
        val message = args.slice(1 until args.size).joinToString(" ")

        // If contactIdOrDash is "-", send message to last sent contact
        if (contactIdOrDash.equals("-")){
            if(LastSentContactId == null){
                println("你还没发送过消息，LastSentContactId不存在")
                return
            }
            botInstance.getFriendOrGroup(LastSentContactId!!).sendMessage(message)
            return
        }
        // Otherwise, send message to the specified contact
        val contactId = contactIdOrDash.toLong()
        botInstance.getFriendOrGroup(contactId).sendMessage(message)
        LastSentContactId = contactId
    }
}

