(ns digger.parser
  (:require [digger.utils :as utils]
            [hickory.core :as hickory]))

(defn parse
  [html-str]
  "Parses HTML into clojure data structures. E.g.:

   (parser/parse '<html><body><a href=\"example.com\">Click me!</a></body></html>')
   {:type :document,
    :content [{:type :element,
               :attrs nil,
               :tag :html,
               :content [{:type :element,
                          :attrs nil,
                          :tag :head,
                          :content nil},
                        {:type :element,
                         :attrs nil,
                         :tag :body,
                         :content [{:type :element,
                                    :attrs {:href example.com},
                                    :tag :a,
                                    :content [Click me!]}]}]}]}
  "
 (hickory/as-hickory (hickory/parse html-str)))

(defn extract-asset
  [html]
  (let [tag (:tag html)]
    (cond (= tag :script) (get-in html [:attrs :src])
          (= tag :img) (get-in html [:attrs :src])
          (and (= tag :link)
               (= (get-in html [:attrs :rel]) "stylesheet"))
            (get-in html [:attrs :href])
          :else nil)))

(defn extract-link
  [html]
  (let [tag (:tag html)]
    (cond (= tag :a) (get-in html [:attrs :href])
          ; TODO: evaluate the need to handle <link rel="anternate"> tags
          ; (and (= tag :link)
          ;      (= (get-in html [:attrs :rel]) "alternate"))
          ;  (get-in html [:attrs :href])
          :else nil)))

(defn append-absolute
  [url collection domain]
  (if url
    (conj collection
          (utils/ensure-absolute url domain))
    collection))

(defn extract-assets-and-links
  "Given a parsed html, this function does a graph traversal in the nodes
   of its DOM, scanning for assets URLs and links as it goes. The traversal
   is done in a breadth-first search fashion, so we can relatively easily
   leverage tail recursion in order to avoid eventual stack overflows.

   The return value is a map such as:
     {:assets SET_OF_ASSET_URIS
      :links SET_OF_LINKS}
  "
  ([html domain]
   (extract-assets-and-links [html] (set []) (set []) domain))

  ([tags assets links domain]
    (if (empty? tags)
      {:assets assets
       :links links}
      (let [tag (first tags)
            asset (extract-asset tag)
            link (extract-link tag)]
        (recur (into (rest tags) (:content tag))
               (append-absolute asset assets domain)
               (append-absolute link links domain)
               domain)))))
