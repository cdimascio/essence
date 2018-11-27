# essence


An automatic web page content extractor for **Kotlin** and **Java**.

Given an HTML document, **essence** automatically identifies and extracts the main text content and more...

This library is heavily influenced by [unfluff-node0(https://github.com/ageitgey/node-unfluff)

## Usage

**Java**

```Java
import io.github.cdimascio.essence.Essence;

String data = Essence.extract(html)
println(data.getText())
```

**Kotlin**

```Kotlin
val data = Essence.extract(html)
println(data.text)
```

## Install

Maven

```xml
```

Gradlew

```groovy
```

## Extracted data elements


**essence** attempts to extract the following content:

- `title` - The document's title
- `softTitle` - A version of `title` with less truncation
- `date` - The document's publication date
- `copyright` - The document's copyright line, if present
- `author` - The document's author
- `publisher` - The document's publisher (website name)
- `text` - The main text of the document with all the junk thrown away
- `image` - The main image for the document (what's used by facebook, etc.)
- *(coming soon...)*`videos` - An array of videos that were embedded in the article. Each video has src, width and height.
- `tags`- Any tags or keywords that could be found by checking &lt;rel&gt; tags or by looking at href urls.
- `canonicalLink` - The [canonical url](https://support.google.com/webmasters/answer/139066?hl=en) of the document, if given.
- `lang` - The language of the document, either detected or supplied by you.
- `description` - The description of the document, from &lt;meta&gt; tags
- `favicon` - The url of the document's [favicon](http://en.wikipedia.org/wiki/Favicon).
- `links` - An array of links embedded within the article text. (text and href for each)


## License

[Apache 2.0](LICENSE)



