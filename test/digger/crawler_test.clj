(ns digger.crawler-test
  (:require [org.httpkit.client :as http]
            [clojure.test :refer :all]
            [digger.crawler :as crawler]
            [digger.utils :as utils]))

(defn make-mock-page
  [content]
  (str
    "<html>
     <body>
     <head>
       <script type='javascript' src='https://cdn.jquery.com/jquery.min.js'></script>
       <link rel='stylesheet' type='text/css' href='main.css'>
       <link rel='alternate' href='/pt-br'>
     </head>
     <body>"
     content
     " <div class='footer'>
         <a href='/'>Home</a>
         <a href='about.html'>About us</a>
         <a href='contact.html'>Contact</a>
       </div>
     </body>
     </html>"))

(def mock-home-page
  (make-mock-page "
       <div>Welcome to example.com!</div>"))

(def mock-about-page
  (make-mock-page "
       <div>Trust me, we're awesome!</div>"))

(def mock-contact-page
  (make-mock-page "
       <div>Shoot us an email at example@example.com!</div>"))

(defn mock-no-op
  "Does nothing"
  [& args])

(defn mock-http-get
  [url]
  (cond (= url "http://example.com")              (atom {:body mock-home-page})
        (= url "http://example.com/about.html")   (atom {:body mock-about-page})
        (= url "http://example.com/contact.html") (atom {:body mock-contact-page})))

(deftest crawl-sync-test
  (testing "It does not crawl repeated urls"
    (with-redefs [http/get mock-http-get
                  utils/debug-print mock-no-op]
      (let [res (crawler/crawl-sync "http://example.com/")
            urls (vec (map :url res))]
        (is (= (count urls) 3))
        (is (some #{"http://example.com"} urls))
        (is (some #{"http://example.com/about.html"} urls))
        (is (some #{"http://example.com/contact.html"} urls)))))

  (testing "It outputs the expected assets"
    (with-redefs [http/get mock-http-get
                  utils/debug-print mock-no-op]
      (let [res         (crawler/crawl-sync "http://example.com/")
            res-by-url  (reduce (fn [acc r] (assoc acc (:url r) r)) {} res)
            home-res    (res-by-url "http://example.com")
            about-res   (res-by-url "http://example.com/about.html")
            contact-res (res-by-url "http://example.com/contact.html")
            expected    #{"https://cdn.jquery.com/jquery.min.js" "http://example.com/main.css"}]
        (is (= (count (:assets home-res)) 2))))))

(deftest crawl-async-test
  (testing "It does not crawl repeated urls"
    (with-redefs [http/get mock-http-get]
      (let [res (crawler/crawl-async "http://example.com/")
            urls (vec (map :url res))]
        (is (= (count urls) 3))
        (is (some #{"http://example.com"} urls))
        (is (some #{"http://example.com/about.html"} urls))
        (is (some #{"http://example.com/contact.html"} urls)))))

  (testing "It outputs the expected assets"
    (with-redefs [http/get mock-http-get]
      (let [res         (crawler/crawl-async "http://example.com/")
            res-by-url  (reduce (fn [acc r] (assoc acc (:url r) r)) {} res)
            home-res    (res-by-url "http://example.com")
            about-res   (res-by-url "http://example.com/about.html")
            contact-res (res-by-url "http://example.com/contact.html")
            expected    #{"https://cdn.jquery.com/jquery.min.js" "http://example.com/main.css"}]
        (is (= (count (:assets home-res)) 2))))))
