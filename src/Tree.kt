import java.util.*

/**
 * Created by Impy on 13.05.17.
 */

class Tree(root: Node)

enum class SIGNS {
    PLUS, MUL, OR, AND, IF, NEG, ANY, EXIST, EQ, NEXT
}

abstract class Node() {
    abstract override fun toString(): String
}

abstract class AbstractNode(open val key: SIGNS) : Node()

class BinaryNode(override val key: SIGNS, val left: Node, val right: Node) : AbstractNode(key) {
    override fun toString(): String {
        return key.toString() + " (" + left.toString() + " " + right.toString() + ")"
    }

}

class UnaryNode(override val key: SIGNS, val son: Node) : AbstractNode(key) {
    override fun toString(): String {
        return key.toString() + " (" + son.toString() + ")"
    }
}

class Leaf(val value: String): Node() {
    override fun toString(): String {
        return value
    }
}

class Function(val name: Leaf, val args: List<Node>) : Node() {
    override fun toString(): String {
        var ans = name.toString() + " ("
        for (it in args) {
            ans += " " + it.toString()
        }
        return ans + ")"
    }

}

class Parser(val string: String) {
    var head : Int = 0
    fun expression() : Node {
        val firstNode = disjunction()
        if (string.length == head || string[head] != '-') {
            return firstNode
        }
        head += 2
        val secondNode = expression()
        return BinaryNode(SIGNS.IF, firstNode, secondNode)
    }

    fun disjunction(): Node {
        var firstNode = conjunction()
        while (string.length != head && string[head] == '|') {
            head++
            val secondNode = conjunction()
            firstNode = BinaryNode(SIGNS.OR, firstNode, secondNode)

        }
        return firstNode
    }

    fun conjunction(): Node {
        var firstNode = unary()
        while (string.length != head && string[head] == '&') {
            head++
            val secondNode = unary()
            firstNode = BinaryNode(SIGNS.AND, firstNode, secondNode)
        }
        return firstNode

    }
    fun unary(): Node {
        when (string[head]) {
            '!' -> {
                head++
                return UnaryNode(SIGNS.NEG, unary())
            }
            '(' -> {
                head++
                val temp = expression()
                head++
                return temp
            }
            '@' -> {
                head++
                val temp = variable()
                return BinaryNode(SIGNS.ANY, temp, unary())
            }
            '?' -> {
                head++
                val temp = variable()
                return BinaryNode(SIGNS.EXIST, temp, unary())
            }
            else ->
                return predicate()
        }

    }
    fun variable(): Leaf {
        var ans: String = string[head++].toString()
        while (string[head].isDigit() && string.length > head) {
            ans += string[head++].toString()
        }
        return Leaf(ans)
    }
    fun predicate(): Node {
        if (string[head].isUpperCase()) {
            val name = variable()
            head++
            val args = ArrayList<Node>()
            while (string[head++] != ')') {
                args.add(term())
            }
            return Function(name, args)
        }
        val firstNode = term()
        if (string[head] != '=') {
            println("asSDSD]\n")
            println(firstNode.toString())
        }
        head++
        return BinaryNode(SIGNS.EQ, firstNode, term())
    }
    fun term(): Node {
        var firstNode = addendum()
        while (string.length != head && string[head] == '+') {
            head++
            val secondNode = addendum()
            firstNode = BinaryNode(SIGNS.PLUS, firstNode, secondNode)
        }
        return firstNode

    }
    fun addendum(): Node {
        var firstNode = multiplied()
        while (string.length != head && string[head] == '*') {
            head++
            val secondNode = multiplied()
            firstNode = BinaryNode(SIGNS.MUL, firstNode, secondNode)
        }
        return firstNode
    }
    fun multiplied(): Node {
        var node: Node
        println(string[head])
        when(string[head]) {
            '(' -> {
                //head++
                node = term()
                //head++
            }
            '0' -> {
                head++
                node = Leaf("0")
            }
            else -> {
                node = variable()
                if (string[head] == '(') {
                    head++
                    val args = ArrayList<Node>()
                    while (string[head++] != ')') {
                        args.add(term())
                    }
                    node = Function(node, args)
                }
            }
        }
        while(string.length > head && string[head] == '\'') {
            node = UnaryNode(SIGNS.NEXT, node)
            head++
        }
        return node
    }
}

fun main(args: Array<String>) {
    for (it in args) {
        val par = Parser(it)
        val node = par.expression()
        println(node.toString())
    }
}