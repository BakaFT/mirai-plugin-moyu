package command;

import me.bakaft.plugin.PluginMain
import me.bakaft.plugin.util.Utils.Companion.getBotInstance
import me.bakaft.plugin.util.Utils.Companion.getFriendByIdOrNickOrRemarkFuzzy
import me.bakaft.plugin.util.Utils.Companion.getGroupMembersByIdFuzzy
import me.bakaft.plugin.util.Utils.Companion.getGroupsByIdOrNameFuzzy import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.ContactUtils.getFriendOrGroup
import net.mamoe.mirai.contact.remarkOrNameCardOrNick
import net.mamoe.mirai.message.data.*

object SendGroupCommand: RawCommand(
    PluginMain,
    "sendg","sg",
    usage = "/s(end)g <群号>|<群名> <消息> 支持模糊匹配群号或群名(不可含空格，未处理此异常，可能会导致发送到错误的群)，消息中可包含空格。",
    description = "向指定的群发送消息，支持模糊匹配群名（不能包含空格，未处理此类异常，请勿使用）或群号。消息中可包含空格",
    prefixOptional = true,
){
    var lastSentGroupId:Long? = null

    @OptIn(ConsoleExperimentalApi::class)
    override suspend fun CommandContext.onCommand(args: MessageChain) {
        val botInstance = getBotInstance()
        if (botInstance == null) {
            println("No Bot instance found")
            return
        }

        if (args.size <= 1) {
            println(usage)
            return
        }

        val groupIdOrDash = args[0].content
        val fuzzySearchResult = getGroupsByIdOrNameFuzzy(idOrName = groupIdOrDash, groups = botInstance.groups)

        // Message can be split by space, so we need to join them from args[1] to args[args.size-1]
        // Such as args[1] = "I",args[2]="AM",args[3]="JOHN", we need to join them to "I AM JOHN"
        // val message = args.slice(1 until args.size).joinToString(" ")
        val chainBuilder = MessageChainBuilder()
        for (i in 1 until args.size) {
            val messagePiece = args[i]
            when{
                messagePiece.content.startsWith("@") -> {
                    if (messagePiece.content == "@all") {
                        chainBuilder.append(AtAll)
                    }else{
                        val groupMemebers = getGroupMembersByIdFuzzy(messagePiece.content.substring(1),fuzzySearchResult[0])
                        when(groupMemebers.size){
                            0 ->{
                                println("Member you wanna at is not found")
                            }
                            1 -> {
                                chainBuilder.append(
                                    At(groupMemebers[0])
                                )
                            }
                            else -> {
                                println("Input is ambiguous, Found ${fuzzySearchResult.size} group members:")
                                groupMemebers.forEach { println("[Group][Name]:" + it.remarkOrNameCardOrNick + ",[ID]:" + it.id) }
                            }
                        }
                    }
                }
                else -> {
                    chainBuilder.append(messagePiece)
                }
            }
        }
        val chain = chainBuilder.build()



        // If groupIdOrDash is "-", send message to last sent group
        if (groupIdOrDash == "-") {
            if (lastSentGroupId == null) {
                println("Have not sent any message yet, lastSentGroupId not set")
                return
            }
            botInstance.getFriendOrGroup(lastSentGroupId!!).sendMessage(chain)
            return
        }
        // Otherwise, Search the Group and send message to it
        when (fuzzySearchResult.size) {
            0 -> {
                println("No group found")
            }
            1 -> {
                botInstance.getGroup(fuzzySearchResult[0].id)?.sendMessage(chain)
                lastSentGroupId = fuzzySearchResult[0].id
            }
            else -> {
                println("Input is ambiguous, Found ${fuzzySearchResult.size} groups:")
                fuzzySearchResult.forEach { println("[Group][Name]:" + it.name + ",[ID]:" + it.id) }
            }
        }
    }
}

object SendFriendCommand: RawCommand(
    PluginMain,
    "sendf","sf",
    usage = "/s(end)g <QQ号>|<昵称|备注> <消息> 支持模糊匹配QQ号或昵称|备注(不可含空格，未处理此异常，可能会导致发送到错误的好友)，消息中可包含空格。",
    description = "向指定的好友发送消息，支持模糊匹配QQ号或昵称|备注(不可含空格，未处理此异常，可能会导致发送到错误的好友)，消息中可包含空格。",
    prefixOptional = true,
){
    private var lastSentFriendId:Long? = null

    @OptIn(ConsoleExperimentalApi::class)
    override suspend fun CommandContext.onCommand(args: MessageChain) {
        val botInstance = getBotInstance()
        if (botInstance == null) {
            println("No Bot instance found")
            return
        }

        if (args.size <= 1) {
            println(usage)
            return
        }

        val friendIdOrDash = args[0].content
        // Message can be split by space, so we need to join them from args[1] to args[args.size-1]
        // Such as args[1] = "I",args[2]="AM",args[3]="JOHN", we need to join them to "I AM JOHN"
        val message = args.slice(1 until args.size).joinToString(" ")

        // If friendIdOrDash is "-", send message to last sent friend
        if (friendIdOrDash == "-") {
            if (lastSentFriendId == null) {
                println("Have not sent any message yet, lastSentFriendId not set")
                return
            }
            botInstance.getFriend(lastSentFriendId!!)?.sendMessage(message)
            return
        }
        // Otherwise, Search the Friend and send message to it
        val fuzzySearchResult = getFriendByIdOrNickOrRemarkFuzzy(idOrNameOrRemark = friendIdOrDash, friends = botInstance.friends)
        when (fuzzySearchResult?.size) {
            0 -> {
                println("No friend found")
            }
            1 -> {
                botInstance.getFriend(fuzzySearchResult[0].id)?.sendMessage(message)
                lastSentFriendId = fuzzySearchResult[0].id
            }
            else -> {
                println("Input is ambiguous, Found ${fuzzySearchResult?.size} friends:")
                fuzzySearchResult?.forEach { println("[Friend][Nick]:" + it.nick + ",[Remark]:" + it.remark + ",[ID]:" + it.id) }
            }
        }
    }
}