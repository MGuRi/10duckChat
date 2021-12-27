package com.sinduck.jotbyungsin.Util

import android.util.Log
import com.sinduck.jotbyungsin.MessagesData
import org.jivesoftware.smack.packet.ExtensionElement
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.util.XmlStringBuilder


class MessageExtension: ExtensionElement {
    private val NAMESPACE: String = "timestamp:time"
    private val ELEMENT = "time"

    private var mTime: String? = null

    private val ATTRIBUTE_REPLY_TEXT = "mTime"

//    override fun toXML(): CharSequence {
//        val xml = XmlStringBuilder(this)
//        xml.attribute(ATTRIBUTE_REPLY_TEXT, getTimeMessage())
//        xml.closeEmptyElement()
//        return xml
//    }


    fun setTimeMessage(_mText: String) {
        mTime = _mText
    }

    fun getTimeMessage(): String? {
        return mTime
    }

    override fun toXML(enclosingNamespace: String?): CharSequence {
        val xml = XmlStringBuilder(this)
        xml.attribute(ATTRIBUTE_REPLY_TEXT, getTimeMessage())
        xml.closeEmptyElement()
        return xml
    }

    override fun getElementName(): String {
        return ELEMENT
    }

    override fun getNamespace(): String {
        return NAMESPACE
    }

    inner class Provider : EmbeddedExtensionProvider<MessageExtension>() {
        override fun createReturnExtension(
            currentElement: String,
            currentNamespace: String,
            attributeMap: Map<String, String>,
            content: List<ExtensionElement?>
        ): MessageExtension {
            val repExt = MessageExtension()
            repExt.setTimeMessage(attributeMap.getValue(ATTRIBUTE_REPLY_TEXT))
            return repExt
        }
    }
}