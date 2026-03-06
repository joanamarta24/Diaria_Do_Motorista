import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.diaria_do_motorista.data.db.remote.enums.SyncStatus
import com.example.diaria_do_motorista.data.db.remote.enums.TipoVeiculo
import com.github.binodnme.dateconverter.converter.DateConverter

@Entity(tableName = "veiculos")
@TypeConverters(DateConverter::class, TipoVeiculoConverter::class)
data class VeiculoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Dados principais
    val placa: String,
    val renavam: String,
    val chassi: String,
    val marca: String,
    val modelo: String,
    val anoFabricacao: Int,
    val anoModelo: Int,
    val cor: String,
    val tipo: TipoVeiculo,

    // Relacionamentos
    val transportadoraId: Long? = null,
    val motoristaId: Long? = null,

    // Documentação
    val documentoNumero: String,
    val dataEmissaoDocumento: String,
    val dataVencimentoDocumento: String,
    val documentoFotoPath: String? = null,

    // Características
    val capacidadeCargaKg: Double,
    val capacidadeCargaM3: Double,
    val numeroEixos: Int,
    val comprimento: Double? = null,
    val altura: Double? = null,
    val largura: Double? = null,

    // Rastreador
    val possuiRastreador: Boolean = false,
    val empresaRastreador: String? = null,
    val numeroSerieRastreador: String? = null,

    // Status
    val ativo: Boolean = true,
    val disponivel: Boolean = true,
    val emViagem: Boolean = false,
    val quilometragem: Int = 0,
    val ultimaRevisaoKm: Int? = null,
    val proximaRevisaoKm: Int? = null,
    val dataUltimaRevisao: String? = null,

    // Controle de sincronização
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val dataSincronizacao: String? = null,

    // Timestamps
    val dataCriacao: String,
    val dataAtualizacao: String? = null,

    // Metadados
    val observacoes: String? = null,
    val fotosAdicionais: String? = null
)