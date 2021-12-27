package com.sinduck.jotbyungsin.Util

import android.util.Log
import com.sinduck.jotbyungsin.MessagesData
import org.jivesoftware.smack.packet.ExtensionElement
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.provider.ProviderManager

object XmppUtil {
    val publicAddr = "39.113.240.156"
    val Domain = "192.168.0.105"

    fun getStringWithDomain(_String: String): String {
        return "${_String}@${Domain}";
    }

    fun getMessageBody(message: Message) {

    }

    fun getMessageBodyWithoutXML(message: Message): MessagesData {
        return MessagesData(
            "호환 오류",
            "",
            message.body.toString()
        )
    }
}