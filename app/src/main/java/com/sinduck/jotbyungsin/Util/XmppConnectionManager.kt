package com.sinduck.jotbyungsin.Util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import java.lang.Exception
import java.net.InetAddress
import javax.net.ssl.HostnameVerifier

object XmppConnectionManager {
    val TAG: String = XmppConnectionManager::class.java.simpleName
    lateinit var mConnection: AbstractXMPPConnection
    lateinit var roster: Roster

    suspend fun setConnection(id: String, pw: String): Boolean {
        try {
            val addr = InetAddress.getByName(XmppUtil.publicAddr)
            val serviceName = JidCreate.domainBareFrom(XmppUtil.Domain)
            val hostnameVerifier = HostnameVerifier { s, sslSession -> false }

            val config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(id, pw)
                .setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setXmppDomain(serviceName)
                .setHostnameVerifier(hostnameVerifier)
                .setHostAddress(addr)
                .build()

            mConnection = XMPPTCPConnection(config).apply {
                connect()
                login()
                Log.d(TAG, "Login Request id:${id}, pw:${pw}")
            }
            roster = Roster.getInstanceFor(mConnection)
            return mConnection.isConnected && mConnection.isAuthenticated
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }
}