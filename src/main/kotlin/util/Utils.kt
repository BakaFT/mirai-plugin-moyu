package me.bakaft.plugin.util

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.*
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
    }


}