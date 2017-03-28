(ns digger.utils
  (:require [clojure.string :as cstring]
            [cemerick.url :as cem]))

(defn debug-print
  "Prints to stderr"
  [& msgs]
  (binding [*out* *err*]
    (apply println msgs)))

(defn remove-duplicate-keys
  "Creates a map and returns its values so duplicate keys are removed"
  [collection key]
  (vals (reduce (fn [acc el] (assoc acc (el key) el)) {} collection)))

(defn same-domain?
  [url1 url2]
  (let [{host1 :host port1 :port} (cem/url url1)
        {host2 :host port2 :port} (cem/url url2)]
    (and (= host1 host2)
         (= port1 port2))))

(defn extract-root-domain
  [url]
  (let [parts (cem/url url)
        {:keys [protocol host port]} parts]
    (str protocol "://" host (if port (str ":" port) ""))))

(defn ensure-leading-slash
  [url]
  (if (cstring/starts-with? url "/")
    url
    (str "/" url)))

(defn remove-trailing-slash
  [url]
  (if (cstring/ends-with? url "/")
    (subs url 0 (dec (.length url)))
    url))

(defn ensure-absolute
  "This function normalizes URLs. It's useful for transforming
   relative urls into absolute URLs. E.g.:

  about             -> root-domain/about
  about/            -> root-domain/about
  /about.html       -> root-domain/about.html
  root-domain/about -> root-domain/about
  "
  [url root-domain]
  (try
    (let [parts (cem/url url)]
      (str parts))
    (catch java.net.MalformedURLException e
      (let [parts (cem/url root-domain)]
        (str (assoc parts :path (remove-trailing-slash (ensure-leading-slash url))))))))
