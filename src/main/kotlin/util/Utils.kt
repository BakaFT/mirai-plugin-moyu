package me.bakaft.plugin.util

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.action.Nudge
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.MiraiInternalApi

class Utils {

    companion object{
        @Suppress("unused")
        fun getGroupsByIdOrNameFuzzy(idOrName:String,groups: ContactList<Group>):List<Group>{
            try {
                // If idOrName is a number, it's a group id
                val longValue = idOrName.toLong()
                // Use startsWith to fuzzy match group id, say input is "123", it will match "123456" but not match "222123"
                return groups.filter { it.id.toString().startsWith(idOrName) }
            }catch (e:Exception){
                // Use contains to fuzzy match group name, say input is "123", it will match "123456" and "222123"
                return  groups.filter { it.name.contains(idOrName) }
            }
        }

        @Suppress("unused")
        fun getFriendByIdOrNickOrRemarkFuzzy(idOrNameOrRemark:String,friends:ContactList<Friend>):List<Friend>?{
            try {
                val longValue = idOrNameOrRemark.toLong()
                return friends.filter { it.id.toString().startsWith(idOrNameOrRemark) }
            }catch (e:Exception){
                return friends.filter { it.nick.contains(idOrNameOrRemark) || it.remark.contains(idOrNameOrRemark) }
            }
        }

        @Suppress("unused")
        fun getBotInstance(): Bot? {
            if (Bot.instances.isEmpty()){
                return null
            }
            return Bot.instances[0]
        }

        @Suppress("unused")
        suspend fun convertMessageChainToPlainTextFriend(chain: MessageChain):String{
            val botInstance = getBotInstance()
            if (botInstance == null){
                return ""
            }
            val res = StringBuilder()
            chain.forEach {
                when(it){
                    is QuoteReply -> {
                        val friendRemarkOrNick = getFriendByIdOrNickOrRemarkFuzzy(it.source.fromId.toString(),botInstance.friends)?.get(0)?.remarkOrNick
                        val quoteMessagePlainText = convertMessageChainToPlainTextFriend(it.source.originalMessage)
                        res.append("[Quote]${friendRemarkOrNick} said ${quoteMessagePlainText}[Quote]")
                    }
                    is Image -> {
                        val url = it.queryUrl()
                        val linkText = "[点击查看图片]"
                        val link = "\u001B]8;;$url\u001B\\$linkText\u001B]8;;\u001B\\"
                        res.append(link)
                    }
                    is FlashImage -> {
                        val url = it.image.queryUrl()
                        val linkText = "[点击查看闪照]"
                        val link = "\u001B]8;;$url\u001B\\$linkText\u001B]8;;\u001B\\"
                        res.append(link)
                    }
                    is Audio -> {
                        val onlineAudio = it as OnlineAudio
                        // The audio file is SILKV3 encoded, can't be played directly
                        val url = onlineAudio.urlForDownload
                        // Buggy, audio length is always 0, maybe a bug of mirai
                        val linkText = "[点击下载语音 ${onlineAudio.length}s]"
                        val link = "\u001B]8;;$url\u001B\\$linkText\u001B]8;;\u001B\\"
                        res.append(link)
                    }
                    is PokeMessage ->{
                        // [戳一戳]
                        res.append(it.content)
                    }
                    is Nudge ->{
                        // 根据文档，看样子是不会遇到了
                        res.append("[Nudge]")
                    }
                    is Face -> {
                        // [表情对应的中文名]
                        res.append(it.content)
                    }
                    is PlainText -> {
                        res.append(it.content)
                    }
                    is At -> {
                        // 好友聊天无效
                    }
                    is AtAll -> {
                        // 好友聊天无效
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
            return res.toString()
        }
    }
}