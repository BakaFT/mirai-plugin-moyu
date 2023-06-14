package command;

import me.bakaft.plugin.PluginMain
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.message.data.MessageChain

object SearchCommand: RawCommand(
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

        val searchResGroup = groups.filter { it.name.contains(neddle)   }
        val searchResFriend = friends.filter { it.nick.contains(neddle) or it.remark.contains(neddle) }

        searchResGroup.forEach {
            println("[Group][Name]:"+it.name+",[ID]:"+it.id)
        }
        searchResFriend.forEach {
            println("[Friend][Name]:"+it.remarkOrNick+",[ID]:"+it.id)
        }
        if(searchResFriend.isEmpty() && searchResGroup.isEmpty()){
            println("Contact Not Found")
        }
    }
}
