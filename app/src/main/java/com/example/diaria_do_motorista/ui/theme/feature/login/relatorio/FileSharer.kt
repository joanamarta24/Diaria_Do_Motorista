import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import jakarta.inject.Inject
import java.io.File

class FileSharer @Inject constructor(
    private val context: Context
) {

    fun compartilharArquivo(file: File, mimeType: String = "application/pdf") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar relatório"))
        } catch (e: Exception) {
            e.printStackTrace()
            throw SharingException("Erro ao compartilhar arquivo: ${e.message}")
        }
    }
}

class SharingException(message: String) : Exception(message)