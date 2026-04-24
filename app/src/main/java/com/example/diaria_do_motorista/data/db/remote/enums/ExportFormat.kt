enum class ExportFormat(val mimeType: String) {
    PDF("application/pdf"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    CSV("text/csv")
}