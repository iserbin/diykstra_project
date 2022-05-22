package com.iserbin.diykstra.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iserbin.diykstra.utils.asLiveData
import java.util.*

class MainViewModel : ViewModel() {

    private var isSuccess: Boolean = false
    private val _textData = MutableLiveData<String>()
    val textData = _textData.asLiveData()
    private lateinit var queue: PriorityQueue<Node>
    private val weights: MutableMap<String, Int> = mutableMapOf()
    private val resultList = mutableListOf<Node>()

    init {
        renderResult(doWork(source))
    }

    fun doWork(s: String): String {
        resultList.clear()
        isSuccess = false
        with(s.parse()) {
            return if (isEmpty()) "" else {
                with(get(0)) {
                    weight = 0
                    weights[name] = weight
                }
                queue = PriorityQueue(size, Node.Comparator)
                    .also { it.addAll(this) }
                while (queue.isNotEmpty()) {
                    queue.poll()?.process(this)
                }
                lastOrNull()?.let { last ->
                    firstOrNull()?.let { first ->
                        resultList.add(last)
                        goToPreviousRecursively(last, first)
                    }
                }
                val empty = resultList.isEmpty()
                if (empty || !isSuccess) "" else renderResultString()
            }
        }
    }

    private fun renderResultString() = StringBuilder().apply {
        resultList.asReversed().forEach { append(it.name) }
    }.toString()

    private fun renderResult(s: String) {
        _textData.value = s.ifBlank { "No way found" }
    }

    private fun List<Node>.goToPreviousRecursively(last: Node?, first: Node) {
        if (last == first) {
            isSuccess = true
            return
        }
        if (last != null) {
            val previous = last.previous
            previous?.let {
                find { it.name == previous }
                    ?.let { nodePrevious ->
                        resultList.add(nodePrevious)
                        goToPreviousRecursively(nodePrevious, first)
                    }
            }
        }
    }

    private fun Node.process(nodes: List<Node>) {
        this.next.forEach { to ->
            nodes.find { it.name == to }?.let { toNode ->
                val weight = (weights[name] ?: weight) + 1
                if (toNode.weight > weight) {
                    toNode.weight = weight
                    toNode.previous = name
                    weights[toNode.name] = weight
                    queue.add(toNode)
                }
            }
        }
    }

    private fun String.parse(): List<Node> {
        return mutableListOf<Node>().apply {
            split(", ").forEach { string ->
                string.replace("[", "")
                    .replace("]", "")
                    .split(":").let { strings ->
                        val name = strings[0].uppercase()
                        val toName = strings[1].uppercase()
                        addOrFillNode(toName, null)
                        addOrFillNode(name, toName)
                    }
            }
        }.sortedBy { it.name }.toList()
    }

    private fun MutableList<Node>.addOrFillNode(
        name: String,
        toName: String?
    ) {
        find { it.name == name }
            ?.next?.let {
                if (!toName.isNullOrBlank()) {
                    it.add(toName)
                }
            } ?: addNode(name, toName)
    }

    private fun MutableList<Node>.addNode(
        name: String,
        toName: String?
    ) {
        Node(
            name = name,
            next = if (toName != null) mutableListOf(toName) else mutableListOf()
        ).let { add(it) }
    }

    companion object {
        private const val source = "[A:B], [A:C], [B:C], [C:D], [D:E]"
    }
}

data class Node(
    val name: String,
    val next: MutableList<String>,
    var previous: String? = null,
    var weight: Int = Int.MAX_VALUE,
) {
    companion object {
        val Comparator = Comparator<Node> { lhs, rhs ->
            lhs.name.compareTo(rhs.name)
        }
    }
}