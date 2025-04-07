package stsa.kotlin_htmx.auth
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID
import stsa.kotlin_htmx.auth.UUIDSerializer

@Serializable
object SessionManager {
    @Contextual val sessionVersion: UUID = UUID.randomUUID()
}