package cs.up.edu.br

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import cs.up.edu.br.data.PersonagemRepository
import cs.up.edu.br.logic.Inimigo
import cs.up.edu.br.logic.SimuladorDeBatalha
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BattleService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var repository: PersonagemRepository
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHARACTER_ID_EXTRA = "character_id"
        private const val CHANNEL_ID = "BattleServiceChannel"
        private const val NOTIFICATION_ID = 1 // ID para a notificação do Foreground Service
        private const val DEATH_NOTIFICATION_ID = 2 // ID para a notificação de morte
    }

    override fun onCreate() {
        super.onCreate()
        // Inicializa dependências (Acessando o Repository via MainApplication)
        val application = application as MainApplication
        repository = application.repository
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val personagemId = intent?.getLongExtra(CHARACTER_ID_EXTRA, -1L)

        if (personagemId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }

        // 1. Inicia o Serviço em Primeiro Plano (Foreground Service)
        startForeground(NOTIFICATION_ID, buildForegroundNotification("Batalha de RPG em andamento..."))

        // 2. Inicia a Simulação na Coroutine (para não bloquear a thread principal)
        serviceScope.launch {
            Log.d("BattleService", "Iniciando batalha para Personagem ID: $personagemId")
            val personagem = repository.getPersonagem(personagemId)

            if (personagem != null) {
                // Configura o Inimigo (Exemplo de um Orc)
                val orc = Inimigo(
                    nome = "Orc Bruto",
                    pontosDeVida = 8,
                    classeDeArmadura = 12,
                    dano = "1d",
                    baseDeAtaque = 1
                )

                val vitoria = SimuladorDeBatalha.simularBatalha(personagem, orc)

                if (!vitoria) {
                    // Personagem MORREU!
                    Log.d("BattleService", "${personagem.nome} MORREU na batalha!")

                    // Deleta o personagem do DB e notifica
                    repository.deletarPersonagem(personagem)
                    showDeathNotification(personagem.nome) // Mostra notificação de morte
                } else {
                    // Personagem VENCEU!
                    notificationManager.notify(DEATH_NOTIFICATION_ID, buildSuccessNotification(personagem.nome))
                }
            } else {
                Log.e("BattleService", "Personagem com ID $personagemId não encontrado.")
            }

            stopSelf(startId)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Log.d("BattleService", "Serviço BattleService destruído.")
    }

    // --- LÓGICA DE NOTIFICAÇÃO ---
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Simulação de Batalha RPG",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificações para batalhas em segundo plano"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Criador Personagem")
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun buildSuccessNotification(personagemNome: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Batalha Finalizada")
            .setContentText("$personagemNome venceu a batalha! 🎉")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }

    /**
     * Mostra a notificação de morte, uma das exigências do projeto.
     */
    private fun showDeathNotification(personagemNome: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val deathNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Personagem Morreu! 💀")
            .setContentText("$personagemNome não sobreviveu à batalha e foi apagado.")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(DEATH_NOTIFICATION_ID, deathNotification)
    }
}