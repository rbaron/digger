(ns digger.utils-test
  (:require [clojure.test :refer :all]
            [digger.utils :as utils]))

(deftest same-domain?-test
  (testing "It returns false for different subdomains"
    (let [url1 "http://subdomain.example.com"
          url2 "http://example.com"]
      (is (not (utils/same-domain? url1 url2))))))

(deftest ensure-leading-slash-test
  (testing "It appends leading slash when it's not present"
    (let [url (utils/ensure-leading-slash "about.html")]
      (is (= url "/about.html"))))

  (testing "It does not append leading slash when it's already present"
    (let [url (utils/ensure-leading-slash "/about.html")]
      (is (= url "/about.html")))))

(deftest remove-trailing-slash-test
  (testing "It removes trailing slash when it's present"
    (let [url (utils/remove-trailing-slash "about/")]
      (is (= url "about"))))

  (testing "It does nothing if there's no trailing slash"
    (let [url (utils/remove-trailing-slash "about")]
      (is (= url "about"))))

  (testing "It does returns an empty string if there's only the slash"
    (let [url (utils/remove-trailing-slash "/")]
      (is (= url "")))))

(deftest ensure-absolute-test
  (testing "It appends root domain to path without leading slash"
    (let [url (utils/ensure-absolute "about.html" "http://example.com")]
      (is (= url "http://example.com/about.html"))))

  (testing "It appends root domain to path without leading slash when domain has trailing slash"
    (let [url (utils/ensure-absolute "about.html" "http://example.com/")]
      (is (= url "http://example.com/about.html"))))

  (testing "It appends root domain to path with leading slash"
    (let [url (utils/ensure-absolute "/about.html" "http://example.com")]
      (is (= url "http://example.com/about.html"))))

  (testing "It appends root domain to path with leading slash when domain has trailing slash"
    (let [url (utils/ensure-absolute "/about.html" "http://example.com/")]
      (is (= url "http://example.com/about.html"))))

  (testing "It returns the url if it's already absolute"
    (let [url (utils/ensure-absolute "https://some-domain.tld/about.html" "http://example.com")]
      (is (= url "https://some-domain.tld/about.html")))))
