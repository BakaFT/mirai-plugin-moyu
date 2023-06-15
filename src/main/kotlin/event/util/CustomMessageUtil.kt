package me.bakaft.plugin.event.util

import me.bakaft.plugin.util.Utils
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.contact.remarkOrNameCardOrNick
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.message.action.Nudge
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl

class CustomMessageUtil {
    companion object{
        @Suppress("unused")
        suspend fun convertMessageChainToPlainTextGroup(chain: MessageChain,topChain:MessageChain=chain):String{
            val botInstance = Utils.getBotInstance()!!
            val res = StringBuilder()
            chain.forEach{
                when(it){
                    is QuoteReply -> {
                        val remarkOrNameCardOrNick = Utils.getGroupsByIdOrNameFuzzy(
                            it.source.targetId.toString(),
                            botInstance.groups
                        )[0].getMember(it.source.fromId)?.remarkOrNameCardOrNick
                        val quoteMessagePlainText = convertMessageChainToPlainTextGroup(it.source.originalMessage,topChain)
                        res.append("[Quote]${remarkOrNameCardOrNick} said ${quoteMessagePlainText}[Quote]")
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
                    is ForwardMessage ->{
                        // CLI难以展示
                        // 也许可以序列化作为URL参数，通过一个静态页面反序列化展示
                        res.append("[转发消息]")
                    }
                    is FileMessage -> {
                        // 群限定
                        val absoluteFile = it.toAbsoluteFile(
                            Utils.getGroupsByIdOrNameFuzzy(
                                chain[MessageSource]?.targetId.toString(),
                                botInstance.groups
                            )[0]
                        )
                        val fileName = absoluteFile?.name
                        val fileSizeInMegaByte = (absoluteFile?.size?.div(1024*1024) ?:0)
                        val linkText = "[点击下载: ${fileName}(${fileSizeInMegaByte}MB)]"
                        val link = "\u001B]8;;${absoluteFile?.getUrl()}\u001B\\$linkText\u001B]8;;\u001B\\"
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
                        res.append(
                            it.getDisplay(
                                Utils.getGroupsByIdOrNameFuzzy(
                                    topChain[MessageSource]?.targetId.toString(),
                                    botInstance.groups
                                )[0]
                            )
                        )
                    }
                    is AtAll -> {
                        res.append(it.content)
                    }
                }

            }
            return res.toString()
        }

        @Suppress("unused")
        suspend fun convertMessageChainToPlainTextFriend(chain: MessageChain):String{
            val botInstance = Utils.getBotInstance()!!

            val res = StringBuilder()
            chain.forEach {
                when(it){
                    is QuoteReply -> {
                        val friendRemarkOrNick = Utils.getFriendByIdOrNickOrRemarkFuzzy(
                            it.source.fromId.toString(),
                            botInstance.friends
                        )?.get(0)?.remarkOrNick
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
                    is ForwardMessage ->{
                        // CLI难以展示
                        // 也许可以序列化作为URL参数，通过一个静态页面反序列化展示
                        res.append("[转发消息]")
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