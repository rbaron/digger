(defproject digger "0.1.0"
  :description "digger: a simple single-domain web crawler"
  :url "https://github.com/rbaron/digger"
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [com.cemerick/url "0.1.1"]
    [cheshire "5.7.0"]
    [hickory "0.7.0"]
    [http-kit "2.2.0"]
  ]
  :main ^:skip-aot digger.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
