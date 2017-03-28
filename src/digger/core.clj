(ns digger.core
  (:gen-class)
  (:require [cheshire.core :as json]
            [digger.crawler :as crawler]))

(defn run-debug
  [url]
  (println (json/generate-string (crawler/crawl-sync url))))

(defn run
  [url]
  (let [crawled  (crawler/crawl-async url)
        json-res (json/generate-string crawled)]
      (println json-res)
      (shutdown-agents)))

(def usage "USAGE: lein run URL [--debug]")

(defn -main
  [& args]
  (if (<= (count args) 0)
    (println usage)
    (let [url (first args)
          extra-args (set (rest args))]
      (if (contains? extra-args "--debug")
        (run-debug url)
        (run url)))))
