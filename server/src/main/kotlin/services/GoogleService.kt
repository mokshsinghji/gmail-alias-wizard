package com.moksh.services

import com.google.api.client.auth.oauth2.Credential
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.SmtpMsa
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import com.moksh.data.User
import io.ktor.server.plugins.di.annotations.Property
import kotlinx.serialization.Serializable

interface GoogleService {
    // Define methods that the GoogleService should implement
    fun getGmailAliases(user: User): List<GmailApiAliasResponse>?
    // fun createGmailAlias(user: User, displayName: String?, emailAlias: String): GmailApiAliasResponse?
}

@Serializable
data class GmailApiAliasResponse(
    val sendAsEmail: String,
    val displayName: String?,
    val isDefault: Boolean,
    val replyToAddress: String?,
    val verificationStatus: String?,
)

class GoogleServiceImpl(
    @Property("auth.google.clientId") val clientId: String,
    @Property("auth.google.clientSecret") val clientSecret: String,
    // @Property("auth.google.appPassword") val appPassword: String,
) : GoogleService {
    override fun getGmailAliases(user: User): List<GmailApiAliasResponse>? {
        // Implement the logic to fetch Gmail aliases for the user
        // This is a placeholder implementation
        val gmail = buildGmailService(user)

        return gmail?.users()?.settings()?.sendAs()?.list("me")
            ?.execute()?.sendAs?.map {
                GmailApiAliasResponse(
                    sendAsEmail = it.sendAsEmail,
                    displayName = it.displayName,
                    isDefault = it.isDefault,
                    replyToAddress = it.replyToAddress,
                    verificationStatus = it.verificationStatus
                )
            }
    }

    // doesn't work
    // override fun createGmailAlias(user: User, displayName: String?, emailAlias: String): GmailApiAliasResponse? {
    //     // Implement the logic to create a Gmail alias for the user
    //     // This is a placeholder implementation
    //     val gmail = buildGmailService(user)
    //
    //     return gmail?.users()?.settings()?.sendAs()?.create("me", com.google.api.services.gmail.model.SendAs()
    //         .setSendAsEmail(emailAlias)
    //         .setDisplayName(displayName).setSmtpMsa(
    //             SmtpMsa()
    //                 .setHost("smtp.gmail.com")
    //                 .setPort(587)
    //                 .setSecurityMode("SSL")
    //                 .setUsername(user.email)
    //                 .setPassword(appPassword)
    //         ))
    //         ?.execute()?.let {
    //             GmailApiAliasResponse(
    //                 sendAsEmail = it.sendAsEmail,
    //                 displayName = it.displayName,
    //                 isDefault = it.isDefault,
    //                 replyToAddress = it.replyToAddress,
    //                 verificationStatus = it.verificationStatus
    //             )
    //         }
    // }

    private fun buildGmailService(user: User): Gmail? {
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        val credential = UserCredentials.newBuilder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setAccessToken(AccessToken(user.googleAccessToken, null))
            .setRefreshToken(user.googleRefreshToken)
            .build()

        val gmail = Gmail.Builder(transport, jsonFactory, HttpCredentialsAdapter(credential))
            .setApplicationName("Google Alias Wizard").build()
        return gmail
    }
}
