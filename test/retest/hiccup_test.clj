(ns retest.hiccup-test
  (:require
   [retest.hiccup :as sut]
   [clojure.test  :refer [deftest testing]]
   [matcho.core   :refer [match]]))

(deftest get-tag-test
  (match (sut/get-tag []) nil?)
  (match (sut/get-tag [:a]) :a)
  (match (sut/get-tag [:a {}]) :a))

(deftest get-attributes-test
  (match (sut/get-attributes []) nil?)
  (match (sut/get-attributes [:a {}]) {})
  (match (sut/get-attributes [:a {} ""]) {}))

(deftest get-id-test
  (match (sut/get-id []) nil?)
  (match (sut/get-id [:a]) nil?)
  (match (sut/get-id [:a#b]) "b")
  (match (sut/get-id [:a#b.c]) "b"))

(deftest stringify-test
  (match (sut/stringify 1) "1")
  (match (sut/stringify "1") "1")
  (match (sut/stringify [:div 1]) "1")
  (match (sut/stringify [:div [:div 1] 2]) "12"))

(deftest find-node-test
  (match
    (sut/find-node
      [:div#foo 1]
      "foo")
    [:div#foo 1])
  (match
    (sut/find-node
      [:div {:id "foo"} 1]
      "foo")
    [:div {:id "foo"} 1])
  (match
    (sut/find-node
      [:div
       [:div 1]
       [:div [:div#foo 2]]]
      "foo")
    [:div#foo 2]))

(deftest datafy-test
  (match
    (sut/datafy [:div {:data-key :a} "1"])
    {:a "1"})
  (match
    (sut/datafy
      [:div
       [:div {:data-key :a} "1"]
       [:div {:data-key :b} "2"]])
    {:a "1" :b "2"})
  (match
    (sut/datafy
      [:div
       [:div {:data-key :a} "1"]
       [:div {:data-key :b} "2"]])
    {:a "1" :b "2"})
  (match
    (sut/datafy
      [:div {:data-object :a}
       [:div {:data-key :b} "1"]
       [:div {:data-key :c} "2"]])
    {:a {:b "1" :c "2"}})
  (match
    (sut/datafy
      [:div {:data-array :a}
       [:div {:data-key :b} "1"]
       [:div {:data-key :b} "2"]])
    {:a [{:b "1"}
         {:b "2"}]})
  (match
    (sut/datafy
      [:div {:data-object :object-1}
       [:div {:data-array :array-1}
        [:div {:data-key :a} "1"]
        [:div {:data-key :a} "2"]]
       [:div {:data-array :array-2}
        [:div {:data-key :b} "1"]
        [:div {:data-key :b} "2"]]])
    {:object-1 {:array-1 [{:a "1"} {:a "2"}]
                :array-2 [{:b "1"} {:b "2"}]}}))
