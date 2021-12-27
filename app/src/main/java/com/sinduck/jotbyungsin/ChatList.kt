package com.sinduck.jotbyungsin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinduck.jotbyungsin.Util.XmppConnectionManager.mConnection
import com.sinduck.jotbyungsin.Util.XmppConnectionManager.roster
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.filter.StanzaFilter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smack.roster.packet.RosterPacket
import org.jivesoftware.smackx.offline.OfflineMessageManager
import org.jxmpp.jid.Jid

class ChatList : AppCompatActivity(), RosterAdapter.RoasterClickListener {
    private var rosterLists: ArrayList<RosterEntry> = ArrayList()
    private lateinit var adapter: RosterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        roster.subscriptionMode = Roster.SubscriptionMode.accept_all
        mConnection.addPacketInterceptor(StanzaListener { packet ->
            if (packet is Presence) {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "PRESENCES" + packet.from,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, object : StanzaFilter {
            override fun accept(stanza: Stanza?): Boolean {
                return stanza is Presence
            }
        })

        roster.addRosterListener(object: RosterListener {
            override fun entriesAdded(addresses: MutableCollection<Jid>?) {
                getBuddies()
            }

            override fun entriesUpdated(addresses: MutableCollection<Jid>?) {
                getBuddies()
            }

            override fun entriesDeleted(addresses: MutableCollection<Jid>?) {
                getBuddies()
            }

            override fun presenceChanged(presence: Presence?) {
                getBuddies()
            }

        })

        val offlineMessageManager = OfflineMessageManager(mConnection)
        val map = offlineMessageManager.messages.groupBy { it.from }
        val presence = Presence(Presence.Type.available)
        mConnection.sendStanza(presence)
        adapter = RosterAdapter(rosterLists, map)
        adapter.setRoasterListener(this)
        chatListRv.layoutManager = LinearLayoutManager(this)
        chatListRv.adapter = adapter
    }

    fun getBuddies() {
        GlobalScope.launch(Dispatchers.IO) {
            rosterLists.clear()
            val entries = roster.entries
            Log.e("Size of Roster :", "" + entries?.size)
            if (entries != null) {
                for (entry in entries) {
                    rosterLists.add(entry)
                    if (entry.type == RosterPacket.ItemType.from) {
                        roster.createEntry(
                            entry.jid,
                            entry.jid.asUnescapedString(),
                            null
                        )
                    }
                }
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
//        val presence = roster.getAllPresences(Config.loginName + "@" + Config.openfire_host_server_SERVICE)
//
//        adapter.setPresence(presence)
        }
    }

    override fun onClick(
        entry: RosterEntry
    ) {
        val intent = Intent(this, ChatLayout::class.java)
        intent.putExtra("user", entry.jid.asUnescapedString())
        Log.d("intent():",entry.jid.asUnescapedString())
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        getBuddies()
    }
}