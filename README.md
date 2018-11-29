# essence

![](https://travis-ci.org/cdimascio/essence.svg?branch=master)![](https://camo.githubusercontent.com/208c24da54eea1ae12f8abed5dcc6b84b6ce8440/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f6c6963656e73652d417061636865253230322e302d626c75652e737667)

An automatic web page content extractor for _Kotlin_ and _Java_.

Given an HTML document, **essence** automatically extracts the main text content (and much more).

[Try out the demo](https://essence.mybluemix.net/index.html) - _a simple webapp to demonstrate essence_

<p align="center">
  <img src="https://raw.githubusercontent.com/cdimascio/essence/master/assets/essence.png" width="450px"/>
</p>


_This library is heavily inspired by [node-unfluff](https://github.com/ageitgey/node-unfluff) and its [lineage](#credits)_

## Usage

**Java**

```Java
import io.github.cdimascio.essence.Essence;

String data = Essence.extract(html);
System.out.println(data.getText());
```

**Kotlin**

```Kotlin
val data = Essence.extract(html)
println(data.text)
// ...
```

See [Extracted data elements](#extracted-data-elements) for additional extracted metadata.

## Install

**Maven**

```xml
<dependency>
  <groupId>io.github.cdimascio</groupId>
  <artifactId>essence</artifactId>
  <version>0.12.6</version>
  <type>pom</type>
</dependency>
```

**Gradle**

```groovy
compile 'io.github.cdimascio:essence:0.12.6'
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


## Credits
- node-unfluff by [https://github.com/ageitgey](ageitgey)
- python-goose by [Xavier Grangier](https://github.com/grangier)
- goose by [Gravity Labs](https://github.com/GravityLabs)

## License

[Apache 2.0](LICENSE)



