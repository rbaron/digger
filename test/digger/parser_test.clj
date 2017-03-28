(ns digger.parser-test
  (:require [clojure.test :refer :all]
            [digger.parser :as parser]))

(def root-domain "http://example.com")

(def html-str
  "<html>
   <body>
   <head>
     <script type='javascript' src='https://cdn.jquery.com/jquery.min.js'></script>
     <link rel='stylesheet' type='text/css' href='main.css'>
     <link rel='alternate' href='/pt-br'>
   </head>
   <body>
     <div>
       <a href='https://facebook.com/my-cool-app'>
         <img src='facebook-icon.png'>
         Visit us on facebook!
       </a>
     </div>
     <div class='footer'>
       <a href='about.html'>About us</a>
       <a href='contact.html'>Contact</a>
     </div>
   </body>
   </html>")

(def html (parser/parse html-str))

(deftest parser-test
  (testing "Extacting assets from html returns a set of assets URLs"
    (let [{:keys [assets links]} (parser/extract-assets-and-links html root-domain)]
      (is (= (count assets) 3))
      (is (contains? assets "https://cdn.jquery.com/jquery.min.js"))
      (is (contains? assets "http://example.com/facebook-icon.png"))
      (is (contains? assets "http://example.com/main.css"))))

  (testing "Extacting links from html returns a set of links"
    (let [{:keys [assets links]} (parser/extract-assets-and-links html root-domain)]
      (is (= (count links) 3))
      (is (contains? links "https://facebook.com/my-cool-app"))
      (is (contains? links "http://example.com/contact.html"))
      (is (contains? links "http://example.com/about.html"))))

  (testing "Extacted links no not include <link> tags"
    (let [{:keys [assets links]} (parser/extract-assets-and-links html root-domain)]
      (is (not (contains? links "http://example.com/pt-br"))))))
