(ns example.core-test
  (:require
   [example.core :as sut]
   [retest.core]
   [re-frame.db]
   [clojure.test :refer :all]
   [matcho.core  :refer [match]]))

(def root [example.core/index])

(deftest main-test
  (reset! re-frame.db/app-db {})

  (testing "match view"
    (match
     (retest.core/datafy root)
     {::sut/info {::sut/name "unknown"}}))

  (testing "fill name"
    (retest.core/fill root ::sut/name-input "Rich"))

  (testing "match view"
    (match
     (retest.core/datafy root)
     {::sut/info {::sut/name "unknown"}}))

  (testing "submit form"
    (retest.core/click root ::sut/submit))

  (testing "match view"
    (match
     (retest.core/datafy root)
     {::sut/info {::sut/name "Rich"}})))
