(ns example.core-test
  (:require
   [example.core :as sut]
   [retest.core]
   [re-frame.db]
   [clojure.test :refer :all]
   [matcho.core  :refer [match]]))

(deftest main-test
  (reset! re-frame.db/app-db {})

  (testing "match view"
    (match
     (retest.core/datafy example.core/index)
     {::sut/info {::sut/name "unknown"}}))

  (testing "fill name"
    (retest.core/fill example.core/index ::sut/name-input "Rich"))

  (testing "match view"
    (match
     (retest.core/datafy example.core/index)
     {::sut/info {::sut/name "unknown"}}))

  (testing "submit form"
    (retest.core/click example.core/index ::sut/submit))

  (testing "match view"
    (match
     (retest.core/datafy example.core/index)
     {::sut/info {::sut/name "Rich"}})))
