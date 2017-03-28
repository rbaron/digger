(ns digger.crawler
  (:require [org.httpkit.client :as http]
            [clojure.string :as string]
            [digger.parser :as parser]
            [digger.utils :as utils]))

(defn should-crawl?
  [link orig-url visited]
  (and (utils/same-domain? link orig-url)
       (not (contains? visited link))))

(defn filter-links
  [links orig-url visited]
  (filter (fn [link] (should-crawl? link orig-url visited))
          links))

(defn crawl-async
  "This is the crawler's workhorse. It does an in-order depth-first
   traversal of the graph in which nodes are webpages and edges are the
   links between them.

   The paralellism is achieved using threads to handle the recusive calls
   to crawl-async async (done with a call to `pmap`). In order to avoid loops
   and wastefully crawling repeated URLs, a reference to a set `visited`
   is kept in all threads.

   This, by itself, would cause numerous race conditions since multiple
   threads would try and modify the `visited` set. In order to achieve
   thread safety, the `atom` special form. This is an abstration on
   mutexes/semaphores which guarantees that no two threads will modify
   `visited` at the same time.

    Note that, even then, there's a tiny chance that the same URL might
    be crawled twice. If two threads crawling the same URL, run the
    `contains?` predicate _before_ any of them has added the URL to the
    `visited` atom. In practice, this should never happen, but, just to
    be on the safe side, there's a call to utils/remove-duplicate-keys that
    handles that possibility correctly before returning the final result.
  "
  ([url]
    (let [res (crawl-async (utils/remove-trailing-slash url) (atom #{}))]
      (utils/remove-duplicate-keys res :url)))

  ([url visited]
    (if (contains? @visited url)
      []
      (do (swap! visited conj url)
          (let [domain (utils/extract-root-domain url)
                response @(http/get url)]
            (if (string? (:body response))
              (let [html (parser/parse (:body response))
                    {:keys [assets links]} (parser/extract-assets-and-links html domain)
                    valid-links (filter-links links url @visited)]
                (conj (flatten (pmap #(crawl-async % visited)
                                valid-links))
                      {:url url :assets assets}))
                []))))))

(defn print-stats
  [urls url visited]
  (utils/debug-print "Visited:"(count visited) "/ URLs:" (count urls) "/ Visiting" url))

(defn crawl-sync
  "This is the synchronous version of our crawler, mainly used for debug.

   It does a tail recursive BFS in the graph in which nodes are the URLs and
   edges are the links between the URLs.

   It outputs debug info to stderr, so you can still pipe the results to a file
   or other program in your terminal.
  "
  ([url]
    (crawl-sync [(utils/remove-trailing-slash url)] [] #{}))

  ([urls results visited]
    (if (empty? urls)
      results
      (let [url (first urls)]
        (if (contains? visited url)
          (recur (rest urls) results visited)
          (do (print-stats urls url visited)
              (let [domain (utils/extract-root-domain url)
                    response @(http/get url)]
                  (if (string? (:body response))
                    (let [html (parser/parse (:body response))
                          {:keys [assets links]} (parser/extract-assets-and-links html domain)
                          valid-links (filter-links links url visited)]
                      (recur (into (rest urls) valid-links)
                             (conj results {:url url :assets assets})
                             (conj visited url)))
                      (recur (rest urls) results visited)))))))))

