package io.fournkoner.netschool.ui.screens.mail.newmsg

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiver
import io.fournkoner.netschool.domain.usecases.mail.GetMessageFileSizeLimitUseCase
import io.fournkoner.netschool.domain.usecases.mail.SendMessageUseCase
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

@HiltViewModel
class NewMessageViewModel @Inject constructor(
    getMessageFileSizeLimitUseCase: GetMessageFileSizeLimitUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    val messageFileSizeLimit = getMessageFileSizeLimitUseCase()

    fun send(
        subject: String,
        receiver: MailMessageReceiver,
        body: String,
        attachments: Map<String, String>,
        context: Context,
        onCompleteListener: () -> Unit
    ) {
        viewModelScope.launch {
            sendMessageUseCase(
                receiver,
                subject,
                body,
                attachments.mapKeys { it.key.toUri().getFile(context) }
            ).onSuccess {
                onCompleteListener()
            }.onFailure {
                @OptIn(UnreliableToastApi::class)
                toast(R.string.error_occurred)
            }
        }
    }

    private fun Uri.getFile(context: Context): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, this)
        val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(this)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }
}
