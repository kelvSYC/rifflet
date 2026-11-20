import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.kotlin.core.collections.listMultimapOf
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.riff.RiffChunk
import com.kelvsyc.rifflet.riff.RiffEncoderCore
import com.kelvsyc.rifflet.riff.RiffFormBodyEncoder
import com.kelvsyc.rifflet.riff.RiffFormChunkParser
import com.kelvsyc.rifflet.riff.RiffLocalChunk
import com.kelvsyc.rifflet.riff.RiffRootEncoder
import com.kelvsyc.rifflet.riff.RiffRootParser
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

class RiffletCoreCompatibilityTest {
    @Test
    fun chunkIdRoundTrips() {
        assertEquals(ChunkId("RIFF"), ChunkId(ChunkId("RIFF").data))
    }

    @Test
    fun riffEncodesAndParses() {
        val form = ChunkId("TEST")
        val name = ChunkId("INAM")

        val encoder = RiffRootEncoder.newEncoder<String> {
            root = RiffRootEncoder.Root(form)
            encoder(
                RiffFormBodyEncoder(
                    RiffEncoderCore.newCore {
                        addLocalEncoder<String>(name) { value, dest -> dest.writeUtf8(value) }
                    },
                ) { value: String -> listMultimapOf(name to value as Any) },
            )
        }
        val parser = RiffRootParser.newParser<String> {
            root = RiffRootParser.Root(form)
            core {
                addFormParser(
                    form,
                    object : RiffFormChunkParser<String> {
                        override fun parse(chunks: ListMultimap<ChunkId, RiffChunk>) =
                            (chunks[name].first() as RiffLocalChunk).data.data.utf8()
                    },
                )
            }
        }

        val buffer = Buffer()
        encoder.encode("rifflet", buffer)
        assertEquals("rifflet", parser.parse(buffer))
    }
}
