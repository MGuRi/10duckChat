package com.sinduck.jotbyungsin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinduck.jotbyungsin.Util.MessageExtension
import com.sinduck.jotbyungsin.Util.XmppConnectionManager.mConnection
import com.sinduck.jotbyungsin.Util.XmppUtil
import com.sinduck.jotbyungsin.Util.XmppUtil.getStringWithDomain
import kotlinx.android.synthetic.main.activity_chat_layout.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.chat.ChatManagerListener
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.ExtensionElement
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.provider.ProviderManager
import org.jxmpp.jid.impl.JidCreate
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*

class ChatLayout : AppCompatActivity() {
    private var mAdapter: Adapter? = null
    private val mMessagesData = ArrayList<MessagesData>()
    private lateinit var currentChat: Chat
    private lateinit var chatManager: ChatManager
    private lateinit var sendTo: String

    private val TAG: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_layout)

        sendTo = intent.getStringExtra("user")!!
        partner.text = sendTo.split("@")[0]
        mAdapter = Adapter(mMessagesData)
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)

        rv.addItemDecoration(decoration)
        rv.layoutManager = manager
        rv.adapter = mAdapter

        chatManager = ChatManager.getInstanceFor(mConnection)
        currentChat = chatManager.chatWith(JidCreate.entityBareFrom(sendTo /** form name@192.168.0.105 **/))
        setMsgListener()

        sendButton.setOnClickListener {
            val messageSend = inputText.text.toString()
            if (messageSend.isNotEmpty()) {
                sendMessage(messageSend)
                inputText.setText("")
            }
        }

    }

    private fun setMsgListener() {
//        currentChat\
//        ChatManagerListener {chat, _ ->
//            chat.addMessageListener { chat, message ->
//                chat.participant
//                Log.d("CHAT", chat.participant.toString())
//            }
//        }
        chatManager.addIncomingListener { from, message, chat ->
            //시간은 메시지에 포함할지 모르겠음 아니면 서버
            if (message != null) {
                Log.d(TAG, message.toString())
                Log.d(TAG, "barejid:${message.from.asBareJid()} || addrPartner:${chat.xmppAddressOfChatPartner}")
                //if 문확인 연구 필요
                if (message.from.asBareJid() == chat.xmppAddressOfChatPartner) {
                    //외부 통신 XMPP 메시지 에러 핸들링
                    val data = try {
                        //왜하는지 모름
                        ProviderManager.addExtensionProvider(
                            MessageExtension().elementName,
                            MessageExtension().namespace,
                            MessageExtension().Provider()
                        )

                        //check for message with time extension
                        val packetExtension: ExtensionElement = message.getExtension(MessageExtension().elementName, MessageExtension().namespace)
                        Log.d("Packet", message.toXML("").toString())
                        val packetTime = packetExtension.toXML("").toString()
                            .replace("xmlns:stream='http://etherx.jabber.org/streams'", "")
                            .replace("<","").replace(">","")
                            .replace("time", "")
                            .replace("xmlns='stamp:'", "").replace("/","")
                            .replace("mTime='","").replace("'","").replace(" ","")
                        val ext = MessageExtension().apply { setTimeMessage(packetTime) }
                        Log.d("time:", packetTime)
//                        ext.setTimeMessage(packet.)
                        Log.e("--->", " ---  LOG REPLY EXTENSION ---")
                        Log.e("--->", ext.toXML("").toString() + "")
                        Log.e(
                            "--->",
                            ext.getTimeMessage().toString() + ""
                        ) //this is custom attribute

                        MessagesData(
                            sendTo.split("@")[0],
                            ext.getTimeMessage().toString(),
                            message.body.toString()
                        )
                    } catch (e: NullPointerException) {
                        XmppUtil.getMessageBodyWithoutXML(message)
                    } catch (e: ClassCastException) {
                        XmppUtil.getMessageBodyWithoutXML(message)
                    }

                    Log.e(TAG, "MSG::" + from + ": " + data.messages)
                    runOnUiThread {
                        mMessagesData.add(data)
                        mAdapter?.notifyItemInserted(mMessagesData.size)
                        rv.scrollToPosition(mMessagesData.size - 1)
                    }

                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "NEW::${message.body}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Log.w("app", chat.toString())
        }
    }

    //메시지 전송
    @SuppressLint("SimpleDateFormat")
    private fun sendMessage(messageSend: String) {
        val chatManager = ChatManager.getInstanceFor(mConnection)

        //adding custom time extension
        val msgExt = MessageExtension()
        msgExt.setTimeMessage(SimpleDateFormat("a hh:mm").format(Date()))

        //Construct Message XML
        val message = Message().apply {
            type = Message.Type.chat
            addBody("MOOTIME","오후무상시")
//            from = mConnection.user
//            to = currentChat.xmppAddressOfChatPartner
            body = messageSend
            addExtension(msgExt)
        }

        Log.e("message --->", message.toXML("").toString())
        try {
            currentChat.send(message)
            val data = MessagesData("나", msgExt.getTimeMessage()?: "", messageSend)
            mMessagesData.add(data)
            mAdapter?.notifyItemInserted(mMessagesData.size)
            rv.scrollToPosition(mMessagesData.size - 1)
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}