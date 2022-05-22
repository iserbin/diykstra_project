package com.iserbin.diykstra.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iserbin.diykstra.utils.asLiveData
import java.util.*

class MainViewModel : ViewModel() {

    private val _textData = MutableLiveData<String>()
    val textData = _textData.asLiveData()
    private lateinit var queue: PriorityQueue<Node>
    private val weights: MutableMap<String, Int> = mutableMapOf()
    private val resultList = mutableListOf<Node>()

    init {
        doWork()
    }

    private fun doWork() {
        with(source.parse()) {
            if (isEmpty()) noWay() else {
                with(get(0)) {
                    weight = 0
                    weights[name] = weight
                }
                queue = PriorityQueue(size, Node.Comparator)
                    .also { it.addAll(this) }
                while (queue.isNotEmpty()) {
                    queue.poll()?.process(this)
                }
                lastOrNull()?.let {
                    resultList.add(it)
                    goToPreviousRecursively(it)
                }
                if (resultList.isEmpty()) noWay() else renderResult()
            }
        }
    }

    private fun renderResult() {
        StringBuilder().apply {
            append("Result way:\n")
            resultList.asReversed().forEachIndexed { index, node ->
                if (index == resultList.size - 1) {
                    append(node.name)
                } else {
                    append("${node.name} -> ")
                }
            }
        }.toString().let { _textData.value = it }
    }

    private fun noWay() {
        _textData.value = "No way found"
    }

    private fun List<Node>.goToPreviousRecursively(node: Node?) {
        if (node != null) {
            val previous = node.previous
            previous?.let {
                find { it.name == previous }
                    ?.let { nodePrevious ->
                        resultList.add(nodePrevious)
                        goToPreviousRecursively(nodePrevious)
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