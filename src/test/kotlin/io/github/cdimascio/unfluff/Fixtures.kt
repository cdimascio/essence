package io.github.cdimascio.unfluff

val htmlExample = """
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="en-US" xmlns="http://www.w3.org/1999/xhtml">

<!--[if IE 8 ]><html class="lt-ie9"><![endif]-->
<!--[if IE 9 ]><html class="lt-ie10"><![endif]-->
<!--[if gt IE 9]><!--><html><!--<![endif]-->

<head>
  <meta charset="utf-8">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <title>Scholastic GO!</title>

  <meta name="keywords">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- Favicon -->
  <link rel="icon" type="image/x-icon" href="/etc/designs/schgo/clientlibs/images/favicon.ico"/>
  <link rel="shortcut icon" type="image/x-icon" href="/etc/designs/schgo/clientlibs/images/favicon.ico"/>

  <!-- Core Init -->



  <!-- CSS Libs -->


<link rel="stylesheet" href="/etc/designs/schgo/clientlibs.css" type="text/css">



  <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css" rel="stylesheet" integrity="sha384-T8Gy5hrqNKT+hzMclPo118YTQO6cYprQmhrYwIiQ/3axmI1hQomh7Ud2hPOy8SP1" crossorigin="anonymous">






  <!--[if lt IE 9]>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/respond.js/1.4.2/respond.min.js"></script>
  <![endif]-->

  <!-- Scholastic Head DTM -->
      <script type="text/javascript">
      (function() {
        if (!navigator.cookieEnabled) {
          return;
        }

        // Generic set cookie
        function setCookie(cname, cvalue, exdays) {
            var d = new Date();
            d.setTime(d.getTime() + (exdays*60*60*1000));
            var expires = "expires="+ d.toUTCString();
            document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
        }
        // Generic get cookie
        function getCookie(cname) {
            var name = cname + "=";
            var decodedCookie = decodeURIComponent(document.cookie);
            var ca = decodedCookie.split(';');
            for(var i = 0; i <ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) == 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        }

        // Default cookie value if user does not have one
        var
          DEFAULT_COOKIE = "%7B%22userType%22%3A%22anonymous%22%2C%22authType%22%3A%22onsite%22%2C%22orgId%22%3A%2200000%22%7D",
          DEFAULT_COOKIE_NAME = "schgo-user-default",
          COOKIE_NAME = "schgo-user";

        // If there's a cookie from backend, set it frontend
        function cookieListener() {
          if (onReq.status === 200) {
            var res = JSON.parse(this.response);
            setCookie(COOKIE_NAME, res.value[0], 1);
            if(res.value[0] != DEFAULT_COOKIE) {
              window.location.reload();
            }
          } else {
            setCookie(COOKIE_NAME, DEFAULT_COOKIE, 1);
            setCookie(DEFAULT_COOKIE_NAME, true, 1);
          }
        }

        // Checks if schgo-user exists, if not, get it from backend
        var
          currentCookie = getCookie(COOKIE_NAME),
          defaultSet = getCookie(DEFAULT_COOKIE_NAME);

        if(!currentCookie || (encodeURIComponent(currentCookie) === DEFAULT_COOKIE && !defaultSet)) {
          var onReq = new XMLHttpRequest();
          onReq.addEventListener("load", cookieListener);
          onReq.open("GET", "/bin/schgo/cookiebuilder?cookie=schgo-user");
          onReq.send();
        }

        if (defaultSet) {
          setCookie(DEFAULT_COOKIE_NAME, "", -1);
        }

      })();
    </script>

  <!-- DTM Script -->

    <script type="text/javascript" src="//assets.adobedtm.com/8805123d051b038294c5858f0e9358891260018d/satelliteLib-ad27edbb17a9c0ea155c352b46135357a02646a3.js"></script>


  <!-- DTM Data Layer -->
  <script type="text/javascript">

    var dumbleData = {
      "domain" : {
        "name" : "schgo",
        "channel" : "schgo",
        "experienceType" : "Content",
        "experience" : "Content",
        "audience" : "Kids"
      },

     "page" : {
        "name" : "",
        "template" : "article",
        "access" : ""
     },

      "article" : {
        "title" : "Intel Science Talent Search",
        "language" : "English",
        "readinglevel" : "four",
        "subjects" : "",
        "type" : "0ta"
      },

      "issue" : "no issue",

      "search" : {
        "keyword" : "",
        "method" : "",
        "pagination" : "",
        "results" : "",
        "sort" : ""
      }
    }
  </script>



</head>


<body data-slpid="atb999h5724">

  <div class="content-header">
  <header class="header">

        <div class="header--top-container">
                <div class="header--top-container__top-bar">
                        <div class="header--top-container__top-bar--logo">




<div class="image-authorable">
  <a class="image-authorable--link" href="/content/schgo/index.html" target="_self">
    <img src="/content/dam/schgo/static/home-schgo-logo.png" class="image-authorable--image" alt="Logo"/>
</a>


</div>

                        </div>

                        <div class="header--top-container__top-bar--links">
                                <div class="linklist">
  <ul>


      <li class="linklist__item ">
        <a class="linklist__item--link" href="/content/schgo/dictionary.html" target="_self">
           <img src="/content/dam/schgo/static/dictionary-icon.png" alt="Dictionaries Icon"/>
          Dictionaries
        </a>

      </li>

      <li class="linklist__item ">
        <a class="linklist__item--link" href="/content/schgo/go-tube-videos.html" target="_self">
           <img src="/content/dam/schgo/static/gotubes-icon.png" alt="GO Tubes Icon"/>
          GO Tube Videos
        </a>

      </li>

      <li class="linklist__item ">
        <a class="linklist__item--link" href="/content/schgo/world-newspapers.html" target="_self">
           <img src="/content/dam/schgo/static/newspaper-icon.png" alt="Newspapers Icon"/>
          World News
        </a>

      </li>

      <li class="linklist__item has-children">
        <a class="linklist__item--link" href="#" target="_self">
           <img src="/content/dam/schgo/static/atlas-icon.png" alt="Atlas Icon"/>
          Atlas
        </a>
        <div class="linklist__item--submenu">
          <a class="linklist__item--link" href="/content/schgo/K/article/mgw/r01/mgwr016.html" target="_self">

            World Atlas
          </a>

          <a class="linklist__item--link" href="/content/schgo/atb-us-map.html" target="_self">

            U.S. Map
          </a>
        </div>
      </li>

  </ul>
</div>

                        </div>

                        <div class="header--top-container__top-bar--search">
                                <p><a href="#" class="advanced-button modal-launch" data-modal-target="search-advanced">Advanced Search</a></p>
                                <div class="search-field">
        <form name="headersearch" target="_top" id="navform" action="/content/schgo/search.html" method="GET">
                <input type="text" name="q" class="search-field--input" placeholder="Search"/><input type="submit" name="submitbutton" value="Search" class="search-field--submit">
                <input type="hidden" name="_charset_" value="UTF-8"/>
        </form>
</div>
                        </div>

                        <a role="button" class="header--top-container__top-bar--toggle" href="#">
                                <i class="fa fa-bars header--top-container__top-bar--toggle__icon-menu" aria-hidden="true"></i>
                                <i class="fa fa-close header--top-container__top-bar--toggle__icon-close" aria-hidden="true"></i>
                                <span class="header--top-container__top-bar--toggle__text-menu">Menu</span>
                                <span class="header--top-container__top-bar--toggle__text-close">Close</span>
                        </a>

                </div>
        </div>
        <div class="header--bottom-container">
                <div class="header--bottom-container__bottom-bar">
                        <div class="header--bottom-container__bottom-bar--links">
                                <div class="header--bottom-container__bottom-bar--links__readaloud">
                                        <div class="readaloud">
    <p class="readaloud-description">Click on a sentence to start Read Aloud</p>
    <a href="#" class="read-aloud-toggle" title="Click here to toggle Read Aloud."><i class="fa fa-volume-up" title="Sounds" aria-hidden="true"></i>&nbsp;<span>Turn Read Aloud On</span></a>
</div>
                                </div>
                                <div class="header--bottom-container__bottom-bar--links__email hidden">
                                        <div class="component--email">
    <div class="component--email_toolbar-button">
        <a href="#" class="modal-launch" data-modal-target="emailModal">E-mail</a>
    </div>

    <div class="component--email_modal modal-backdrop" id="emailModal">
        <div class="modal-content">
            <div class="modal-header">
                <span class="modal-close">&times;</span>
            </div>
            <div class="modal-body">
                <form name="emailform" data-article-path="/content/schgo/B/article/atb/999/atb999h5724.email.html" method="GET" class="email-form">
                    <h2>Please enter an E-Mail address</h2>
                    <p>Only provide the email address of a friend who would be interested in receiving this article.</p>
                    <input type="text" name="email" class="input-email" placeholder="E-mail Address" id="emailInput"/>
                    <input type="button" name="cancelbutton" value="Cancel" class=" input-cancel modal-close">
                    <input type="submit" name="submitbutton" value="Submit" class="input-submit" id="emailSubmit" disabled>
                    <p>Citations will automatically be included with your content.</p>
                    <p>Please read the <a href="http://www.scholastic.com/edtechprivacy.htm">Scholastic Privacy Policy</a> before e-mailing the article.</p>
                </form>
            </div>
            <div class="component--email_modal--success">
                <h3>Your email was sent successfully!</h3>
            </div>
        </div>
    </div>
</div>


                                </div>
                                <div class="header--bottom-container__bottom-bar--links__standards" data-slpid="atb999h5724">
                                        <a href="/content/schgo/correlations.html?slp_id=atb999h5724">Standards alignment</a>
                                </div>
                                <div class="header--bottom-container__bottom-bar--toc-icon hidden">
                                        <a id="toc-button"><i class="fa fa-bars" title="ToC Icon" aria-hidden="true"></i>&nbsp;Table of Contents</a>
                                </div>
                        </div>
                </div>
        </div>

        <div class="modal-backdrop" id="search-advanced">
    <div class="modal-content">

        <div class="modal-header">
            <h2>Advanced Search</h2>
            <span class="modal-close">&times;</span>
        </div>

        <div class="modal-body">

            <form class="search-advanced-form" name="search-advanced" target="_top" action="/content/schgo/search.html" method="GET">

              <p>Refine the search results returned by selecting any of the following filters:</p>

              <div class="row inline-input">

                <label>
                  <input type="radio" name="searchcontent" value="all" id="full-text" checked>
                  <span class="full-text">Search Full Text</span>
                </label>

                <label>
                  <input type="radio" name="searchcontent" value="title" id="title-only">
                  <span class="title-only">Search Titles Only</span>
                </label>

              </div>

              <p>Filter by...</p>

              <label>
                <h5>All of these words:</h5>
                <input type="text" name="and" id="search-and" placeholder="Type the important words you want included: china wall history">
              </label>


              <div class="row inline-input search-proximity">
                <label>
                  Optional:
                  <input type="checkbox" name="proximityenabled">
                </label>

                <label for="search-proximity">
                  within
                  <input type="number" name="proximity" id="search-proximity">
                  words
                </label>
              </div>

              <div class="row">
                <label for="search-exact">
                  <h5>this exact word or phrase:</h5>
                  <input type="text" name="exact" id="search-exact" placeholder="Enter the exact words you want included, e.g. great wall">
                </label>
              </div>

            <div class="row">
              <label for="search-or">
                <h5>any of the words:</h5>
                <input type="text" name="or" id="search-or" placeholder="Enter the words you want included separated by a space: civil war">
              </label>
            </div>

            <div class="row">
              <label for="search-not">
                <h5>none of these words:</h5>
                <input type="text" name="not" id="search-not" placeholder="Enter the words you don't want included separated by a space: ancient war">
              </label>
            </div>


              <div class="row inline-input">
                  <label>
                    <h5>Lexile Number</h5>
                    <input type="number" name="lexilestart" id="search-lexile-from" placeholder="e.g. 1200">
                 </label>

                  <label for="search-lexile-to">to
                    <input type="number" name="lexileend" id="search-lexile-to" placeholder="e.g. 1700">
                   </label>
              </div>

                <input type="hidden" name="advanced" value="true">
                <input type="submit" value="Search" class="search-advanced--submit">
            </form>
        </div>
    </div>
</div>


</header>
</div>

<div class="component--back-to-top">
        <div class="back-to-top--icon">
        </div>
        <p>Top of Page</p>
</div>


<main class="main-container article-template article">
  <section class="main-content">

    <div class="main-content--column-toc toc-disabled">
      <div class="component--toc">
        <div class="toc-header">
                <i class="fa fa-times toc-close" title="Open Table of Contents" aria-hidden="true"></i>
                <h2 class="toc-title">Table of Contents</h2>
        </div>
        <ul class="toc-list"></ul>
</div>

    </div>

    <div class="main-content--column-primary">



    <h1 class="article-title-main">Intel Science Talent Search</h1>


        <em>Howdy</em>

      <div class="lexile" data-reading-level="four">
        <a href="#" class="lexile-link">Lexile</a>
        <span class="lexile-tooltip">Lexile: 1340</span>
</div>
      <p class="cite-this-article"><a href="#" id="cite-this-article">Cite This Article</a></p>

<ul class="inline-facts"><li><strong>What</strong>: Prestigious national high-school science contest</li><li><strong>Where</strong>: Washington, D.C.</li><li><strong>When</strong>: Every March</li></ul><p>The Intel Science Talent Search (STS) is the top high-school science contest in the United States. This annual competition seeks to encourage talented students to pursue careers in science, mathematics, engineering, and medicine. The level of independent research submitted by these promising young minds is amazing. Entries often delve into exciting areas of scientific research—ranging from an in-depth study of pollution in a Utah river to an experiment determining the behavior of dust particles on Mars. It's no wonder that the STS has earned the nickname of "the junior Nobel Prize."</p><p>Each year, more than 1,500 students from across America submit entries that include a paper describing their research project, as well as a lengthy entry form demonstrating their overall achievement in science or math. Many competitors spend years on their projects, often with the encouragement and guidanceof teachers. From the field of hopefuls, 300 semifinalists are chosen. Each of them receives a ${'$'}1,000 prize along with another ${'$'}1,000 to be used for science or math education in their high school. Then in March, 40 finalists travel to Washington, D.C., where they present their projects to eminent judges, visiting scientists, political dignitaries, and their peers. The top 10 finalists receive scholarships starting at ${'$'}20,000, with the winner receiving ${'$'}100,000. </p><p>The STS began in 1942 as the brainchild of Watson Davis, a director of the Science Service—which still administers the contest—and G. Edward Pendray, a Westinghouse executive who had his company provide the funding. The contest was often called "The Westinghouse," until sponsorship duties were taken over by Intel in 1998. Intel increased the scholarship amounts significantly, but it is the quality of the students that makes this competition so notable. Former finalists have gone on to collect six Nobel Prizes, two Fields Medals, three National Medals of Science, and 10 MacArthur Fellowships, and have contributed immeasurably to the practice and study of all branches of science and mathematics. </p>

      <div class="component--citations" id="citations">

  <h2>How to cite this article:</h2>

  <h3 class="icitation">MLA (Modern Language Association) style:</h3>
  <p class="icite">  "Intel Science Talent Search."  Scholastic GO!, go.scholastic.com/content/schgo/B/article/atb/999/atb999h5724.html. Accessed <span class="date-MLA"></span>.</p>


  <h3 class="icitation">Chicago Manual of Style:</h3>
  <p class="icite">  "Intel Science Talent Search."  Scholastic GO!. https://go.scholastic.com/content/schgo/B/article/atb/999/atb999h5724.html (accessed <span class="date-APA"></span>).</p>


  <h3 class="icitation">APA (American Psychological Association) style:</h3>
  <p class="icite">  (<span class="date-year"></span>). Intel Science Talent Search. Retrieved <span class="date-APA"></span>, from Scholastic GO!. https://go.scholastic.com/content/schgo/B/article/atb/999/atb999h5724.html</p>
</div>
    </div>

    <div class="main-content--column-secondary">







      <div class="component--image-modal">
     <ul class="component--image-modal__sidebar-images">

        <li>
                   <a href="/content/schgo/B/article/atb/p78/atbp7820.html"><img src="/content/dam/schgo/B/0mp/atbp7820.jpg" alt="Shannon Babb, the 2006 Intel Science Talent Search (STS) winner, explains her research project investigating pollution in the Spanish Fork River in Utah. The STS, the top high-school science contest in the United States, is often called &#34;the junior Nobel Prize.&#34;"></a>
         </li>
       </sy>
      </ul>


        <div class="modal-backdrop" id="moreImages">
            <div class="modal-content">
                <div class="modal-header">
                                <h3>Images</h3>
                    <span class="modal-close">&times;</span>
                </div>

                <div class="modal-body">
                             <ul>
             <li>
                <a href="/content/schgo/B/article/atb/p78/atbp7820.html"><img src="/content/dam/schgo/B/0mp/atbp7820.jpg" alt="Shannon Babb, the 2006 Intel Science Talent Search (STS) winner, explains her research projectinvestigating pollution in the Spanish Fork River in Utah. The STS, the top high-school science contest in the United States, is often called &#34;the junior Nobel Prize.&#34;"></a>
                                <p><a href="/content/schgo/B/article/atb/p78/atbp7820.html">Intel Science Talent Search</a></p>
              </li>
          </ul>
                </div>

            </div>
        </div>
</div>

      <div class="quick-links">








        <div class="component--websites quick-links-item">


            <a href="/content/schgo/B/article/atb/999/atb999h5724.gii.html"><span class="icon-left icon-websites"></span><span class="text-websites">Web Sites</span><span class="icon-right fa fa-chevron-right"></span></a>

</div>

      </div>
    </div>

    <footer class="content-footer">
      <div class="footer">
        <div class="footer-container">
                <div class="footer-container--logo">




<div class="image-authorable">



    <img src="/content/dam/schgo/static/schgo-footer-logo.jpg" class="image-authorable--image" alt="Footer Logo"/>

</div>

                </div>
                <div class="footer-container--copyright">
                        <p>&trade; &reg; &amp; &copy; 2018 Scholastic Inc. All Rights Reserved.</p>
                </div>
                <div class="footer-container--linklist">
                        <div class="linklist">
  <ul>


      <li class="linklist__item ">
        <a class="linklist__item--link" href="/content/schgo/about.html" target="_self">

          About
        </a>

      </li>

      <li class="linklist__item ">
        <a class="linklist__item--link" href="/content/schgo/librarians-and-educators.html" target="_self">

          Librarians/Educators
        </a>

      </li>

      <li class="linklist__item ">
        <a class="linklist__item--link" href="http://golla.grolier.com/terms" target="_self">

          Terms of Use
        </a>

      </li>

      <li class="linklist__item ">
        <a class="linklist__item--link" href="http://www.scholastic.com/edtechprivacy.htm" target="_self">

          PRIVACY POLICY
        </a>

      </li>

  </ul>
</div>

                </div>
        </div>
</div>

    </footer>

  </section>
</main>






<script type="text/javascript" src="/etc/clientlibs/granite/jquery/granite/csrf.js"></script>





<script type="text/javascript" src="/etc/designs/schgo/clientlibs.js"></script>







  <!-- Scholastic Footer DTM -->

    <script type="text/javascript">
      _satellite.pageBottom();
    </script>


</body>
</html>
""".trimIndent()