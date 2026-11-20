import com.kelvsyc.rifflet.civ3.Civ3RootParser
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

class RiffletCiv3CompatibilityTest {
    @Test
    fun parsesMinimalFile() {
        // Smallest file Civ3RootParser accepts: "BIC " magic and a 732-byte VER# section (format version 12.7).
        val source = Buffer().apply {
            writeUtf8("BIC ")
            writeUtf8("VER#")
            writeIntLe(1)
            writeIntLe(720)
            writeIntLe(0)
            writeIntLe(0)
            writeIntLe(12)
            writeIntLe(7)
            write(ByteArray(640))
            write(ByteArray(64))
        }
        val file = Civ3RootParser.parse(source)
        assertEquals(12, file.header.major)
        assertEquals(7, file.header.minor)
        assertEquals(emptyList(), file.sections)
    }
}
