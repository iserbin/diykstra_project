import com.iserbin.diykstra.ui.main.MainViewModel
import junit.framework.Assert.assertTrue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on

class MainViewModelSpec : Spek({

    describe("MainViewModel") {

        val testData = mutableMapOf(
            "[A:B], [B:C], [C:D], [D:E]" to "ABCDE",
            "[A:B], [A:C], [B:C], [C:D], [D:E]" to "ACDE",
            "[A:B], [A:C], [B:C], [C:E], [D:E]" to "ACE",
            "[A:B], [A:C], [B:E], [C:D], [D:E]" to "ABE",
            "[A:E], [A:C], [B:E], [C:D], [D:E]" to "AE",
            "[A:B], [C:E], [D:E]" to ""
        )

        on("testing find path method") {
            val viewModel = MainViewModel()
            testData.onEach { entry ->
                val work = viewModel.doWork(entry.key)
                assertTrue(work == entry.value)
            }
        }
    }
})