package io.github.cdimascio.unfluff

import org.jsoup.Jsoup
import org.junit.Test
import kotlin.test.assertEquals


class ExtractractorSpec {
    @Test
    fun returnsABlankTitle() {
        val html = "<html><head><title></title></head></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).title()
        assertEquals("", title)
    }

    @Test
    fun returnsASimpleTitle() {
        val html = "<html><head><title>This is my page</title></head></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).title()
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsASimpleChunkTitle() {
        val html = "<html><head><title>This is my page - mysite</title></head></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).title()
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsSoftTitleWithoutTruncation() {
        val html = "<html><head><title>University Budgets: Where Your Fees Go | Top Universities</title></head></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).softTitle()
        assertEquals("University Budgets: Where Your Fees Go", title)
    }

    @Test
    fun titlePrefersTheMetaTag() {
        val html = "<html><head><title>This is my page - mysite</title><meta property=\"og:title\" content=\"Open graph title\"></head></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).title()
        assertEquals("Open graph title", title)
    }

    @Test
    fun fallsbackToTitleIfEmptyMetatag() {
        val html = "<html><head><title>This is my page - mysite</title><meta property=\"og:title\" content=\"\"></head></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).title()
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsAnotherSimpleTitleChunk() {
        val html = "<html><head><title>coolsite.com: This is my page</title></head></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).title()
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsAnotherSimpleTitleChunkWithoutJunk() {
        val html = "<html><head><title>coolsite.com: &#65533; This&#65533; is my page</title></head></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).title()
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsTheFirstTitle() {
        val html = "<html><head><title>This is my page</title></head><svg xmlns=\"http://www.w3.org/2000/svg\"><title>svg title</title></svg></html>"
        val d = Jsoup.parse(html)
        val title = Extractor(d, Language.en).title()
        assertEquals("This is my page", title)
    }
}


//test 'handles missing favicons', ->
//doc = cheerio.load("<html><head><title></title></head></html>")
//favicon = extractor.favicon(doc)
//eq undefined, favicon
//
//test 'returns the article published meta date', ->
//doc = cheerio.load("<html><head><meta property=\"article:published_time\" content=\"2014-10-15T00:01:03+00:00\" /></head></html>")
//date = extractor.date(doc)
//eq date, "2014-10-15T00:01:03+00:00"
//
//test 'returns the article dublin core meta date', ->
//doc = cheerio.load("<html><head><meta name=\"DC.date.issued\" content=\"2014-10-15T00:01:03+00:00\" /></head></html>")
//date = extractor.date(doc)
//eq date, "2014-10-15T00:01:03+00:00"
//
//test 'returns the date in the <time> element', ->
//doc = cheerio.load("<html><head></head><body><time>24 May, 2010</time></body></html>")
//date = extractor.date(doc)
//eq date, "24 May, 2010"
//
//test 'returns the date in the <time> element datetime attribute', ->
//doc = cheerio.load("<html><head></head><body><time datetime=\"2010-05-24T13:47:52+0000\">24 May, 2010</time></body></html>")
//date = extractor.date(doc)
//eq date, "2010-05-24T13:47:52+0000"
//
//test 'returns nothing if date eq "null"', ->
//doc = cheerio.load("<html><head><meta property=\"article:published_time\" content=\"null\" /></head></html>")
//date = extractor.date(doc)
//eq date, null
//
//test 'returns the copyright line element', ->
//doc = cheerio.load("<html><head></head><body><div>Some stuff</div><ul><li class='copyright'><!-- // some garbage -->© 2016 The World Bank Group, All Rights Reserved.</li></ul></body></html>")
//copyright = extractor.copyright(doc)
//eq copyright, "2016 The World Bank Group"
//
//test 'returns the copyright found in the text', ->
//doc = cheerio.load("<html><head></head><body><div>Some stuff</div><ul>© 2016 The World Bank Group, All Rights Reserved\nSome garbage following</li></ul></body></html>")
//copyright = extractor.copyright(doc)
//eq copyright, "2016 The World Bank Group"
//
//test 'returns nothing if no copyright in the text', ->
//doc = cheerio.load("<html><head></head><body></body></html>")
//copyright = extractor.copyright(doc)
//eq copyright, null
//
//test 'returns the article published meta author', ->
//doc = cheerio.load("<html><head><meta property=\"article:author\" content=\"Joe Bloggs\" /></head></html>")
//author = extractor.author(doc)
//eq JSON.stringify(author), JSON.stringify(["Joe Bloggs"])
//
//test 'returns the meta author', ->
//doc = cheerio.load("<html><head><meta property=\"article:author\" content=\"Sarah Smith\" /><meta name=\"author\" content=\"Joe Bloggs\" /></head></html>")
//author = extractor.author(doc)
//eq JSON.stringify(author), JSON.stringify(["Sarah Smith", "Joe Bloggs"])
//
//test 'returns the named author in the text as fallback', ->
//doc = cheerio.load("<html><head></head><body><span class=\"author\"><a href=\"/author/gary-trust-6318\" class=\"article__author-link\">Gary Trust</a></span></body></html>")
//author = extractor.author(doc)
//eq JSON.stringify(author), JSON.stringify(["Gary Trust"])
//
//test 'returns the meta author but ignore "null" value', ->
//doc = cheerio.load("<html><head><meta property=\"article:author\" content=\"null\" /><meta name=\"author\" content=\"Joe Bloggs\" /></head></html>")
//author = extractor.author(doc)
//eq JSON.stringify(author), JSON.stringify(["Joe Bloggs"])
//
//test 'returns the meta publisher', ->
//doc = cheerio.load("<html><head><meta property=\"og:site_name\" content=\"Polygon\" /><meta name=\"author\" content=\"Griffin McElroy\" /></head></html>")
//publisher = extractor.publisher(doc)
//eq publisher, "Polygon"
//
//test 'returns nothing if publisher eq "null"', ->
//doc = cheerio.load("<html><head><meta property=\"og:site_name\" content=\"null\" /></head></html>")
//publisher = extractor.publisher(doc)
//eq publisher, null
//
//test 'returns nothing if image eq "null"', ->
//doc = cheerio.load("<html><head><meta property=\"og:image\" content=\"null\" /></head></html>")
//image = extractor.image(doc)
//eq image, null
