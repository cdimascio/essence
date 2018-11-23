//package io.github.cdimascio.unfluff
//
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Element
//import org.jsoup.nodes.Node
//import org.jsoup.nodes.TextNode
//import org.jsoup.parser.Parser
//
//fun htmlToElement(html: String): Node {
//    // xml parser may break certain features
//    // htmlParser adds the html body tags
//    val cleanHtml = html.trim()
//    // TODO will this fail if a code block or something started with a '<'
//    if (!cleanHtml.startsWith("<")) {
//        return TextNode(html)
//    }
//    val (tag) = """^<(.*?)>""".toRegex().find(cleanHtml)?.destructured
//        ?: throw IllegalArgumentException("bad html. element detected, but not element")
//    val element = Jsoup.parse(cleanHtml, "", Parser.xmlParser())
//    element.tagName(tag)
//    return element
//}
//
//fun reparent(element: Element) {
////    val html = ("<div>"
////        + "<outer-tag> Some Text <inner-tag> Some more text</inner-tag></outer-tag>"
////        + "</div>")
////
////    val doc = Jsoup.parseBodyFragment(html)
//
////    for (_div in doc.select("div")) {
////    for (_div in element.childNodes()) {
////        // get the unwanted outer-tag
////        val outerTag = _div.select("outer-tag").first()
//
//        // delete any TextNodes that are within outer-tag
//        for (child in element.childNodes()) {
//            if (child is TextNode) child.remove()
//        }
//
//        // unwrap to remove outer-tag and move inner-tag to child of parent div
//        element.unwrap()
//
//        // print the result
////        System.out.println(_div)
////    }
//
//}
