# essence
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

![](https://travis-ci.org/cdimascio/essence.svg?branch=master) [![Maven Central](https://img.shields.io/maven-central/v/io.github.cdimascio/essence.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.cdimascio%22%20AND%20a:%22essence%22) ![](https://camo.githubusercontent.com/208c24da54eea1ae12f8abed5dcc6b84b6ce8440/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f6c6963656e73652d417061636865253230322e302d626c75652e737667)

An automatic web page content extractor for _Kotlin_ and _Java_.

Given an HTML document, **essence** automatically extracts the main text content (and much more).

[Try out the demo](https://essence.mybluemix.net) - _a simple webapp to demonstrate essence_.

<p align="center">
  <img src="https://raw.githubusercontent.com/cdimascio/essence/master/assets/essence.png" width="400px"/>
</p>

_This library is inspired by [node-unfluff](https://github.com/ageitgey/node-unfluff) and its [lineage](#credits)_

## Usage

**Java**

```Java
import io.github.cdimascio.essence.Essence;

EssenceResult data = Essence.extract(html);
System.out.println(data.getText());
```

**Kotlin**

```Kotlin
val data = Essence.extract(html)
println(data.text)
```

See [Extracted data elements](#extracted-data-elements) for additional extracted metadata.

## Install

**Maven**

```xml
<dependency>
  <groupId>io.github.cdimascio</groupId>
  <artifactId>essence</artifactId>
  <version>0.13.0</version>
  <type>pom</type>
</dependency>
```

**Gradle**

```groovy
compile 'io.github.cdimascio:essence:0.13.0'
```

## Try the Essence web demo

[Essence web](https://essence.mybluemix.net) is a simple web page that fetches content at a given url and passes the HTML to this essence library.

![](https://raw.githubusercontent.com/cdimascio/essence/master/assets/example.png)

The essence web project lives [here](https://github.com/cdimascio/essence-web)

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


<a href="https://www.buymeacoffee.com/m97tA5c" target="_blank"><img src="https://bmc-cdn.nyc3.digitaloceanspaces.com/BMC-button-images/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://cleymax.fr/"><img src="https://avatars3.githubusercontent.com/u/24879740?v=4" width="100px;" alt=""/><br /><sub><b>Clément P.</b></sub></a><br /><a href="https://github.com/cdimascio/essence/commits?author=Cleymax" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!