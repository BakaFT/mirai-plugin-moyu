package command;

import me.bakaft.plugin.PluginMain
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.message.data.MessageChain

object ObjCommand: RawCommand(
    PluginMain,
    "search",
    usage = "search <neddle>",
    description = "Search contact by neddle",
    prefixOptional = true,
){
    override suspend fun CommandContext.onCommand(args: MessageChain) {
        if(args.size != 1){
            println(usage)
            return
        }
        val neddle = args[0].toString()
        val botInstance = Bot.instances[0]

        val friends = botInstance.friends
        val groups = botInstance.groups
        val searchResGroup = groups.find { it.name.contains(neddle) }
        val searchResFriend = friends.find { it.nick.contains(neddle) }
        if (searchResGroup != null){
            println("[Name]:"+searchResGroup.name+",[ID]:"+searchResGroup.id)
        }
        if (searchResFriend != null){
            println("[Name]:"+searchResFriend.nick+",[ID]:"+searchResFriend.id)
        }
        if(searchResFriend == null && searchResGroup == null){
            println("Contact Not Found")
        }
    }
}
