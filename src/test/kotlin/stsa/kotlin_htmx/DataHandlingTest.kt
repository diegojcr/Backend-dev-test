package stsa.kotlin_htmx

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.testing.*
import org.junit.Test
import org.xmlunit.assertj3.XmlAssert

class DataHandlingTest {
    @Test
    fun shouldGenerateXml() = testApplication {
        application {
            module()
        }

        client.get("/xml").apply {
            XmlAssert.assertThat(this.body<String>()).isValid
        }
    }
}