(ns retest.core-test
  (:require
   [retest.core  :as sut]
   [matcho.core  :refer [match]]
   [clojure.test :refer [deftest testing]]))

(deftest get-node-attributes-test
  (match
   (sut/get-node-attributes [:div])
   nil)
  (match
   (sut/get-node-attributes [:div 1])
   nil)
  (match
   (sut/get-node-attributes [:div {:id "1"}])
   {:id "1"}))

(deftest get-child-nodes-test
  (match
   (sut/get-child-nodes [:div])
   ^:matcho/strict [])
  (match
   (sut/get-child-nodes [:div {}])
   ^:matcho/strict [])
  (match
   (sut/get-child-nodes [:div {} 1])
   ^:matcho/strict [1])
  (match
   (sut/get-child-nodes [:div 1])
   ^:matcho/strict [1])
  (match
   (sut/get-child-nodes [:div 1 1])
   ^:matcho/strict [1 1]))

(deftest render-test
  (testing "form-1"
    (match
     (sut/render [(fn [] [:div])])
     [:div])
    (match
     (sut/render [(fn [a] [:div a]) 1])
     [:div 1]))
  (testing "form-2"
    (match
     (sut/render [(fn [a] (fn [b] [:div a b])) 1])
     [:div 1 1])))

(deftest find-node-test
  (testing "this"
    (match
     (sut/find-node
      [:div#1 "a"]
      "1")
     [:div#1 "a"]))
  (testing "nested"
    (match
     (sut/find-node
      [:div
       [:div#1 "a"]]
      "1")
     [:div#1 "a"]))
  (testing "by attributes"
    (match
     (sut/find-node
      [:div
       [:div {:id "2"} "b"]]
      "2")
     [:div {:id "2"} "b"]))
  (testing "nested list"
    (match
     (sut/find-node
      [:div
       (for [i ["1" "2" "3"]] ^{:key i}
         [:div {:id i} i])]
      "2")
     [:div {:id "2"} "2"]))
  (testing "meta"
    (match
     (meta
      (sut/find-node [:a [:b [:i#1 "a"]]] "1"))
     {:parents
      [[:b [:i#1 "a"]]
       [:a [:b [:i#1 "a"]]]]})))

(deftest stringify
  (match
   (sut/get-content [:div#1] "1")
   "")
  (match
   (sut/get-content [:div#1 "a"] "1")
   "a")
  (match
   (sut/get-content
    [:div#1 
     [:div "a"]
     [:div "b"]]
    "1")
   "ab")
  (match
   (sut/get-content
    [:div
     [:div#1 
      [:div "a"]
      [:div "b"]]
     [:div "c"]]
    "1")
   "ab"))

(deftest click
  (def counter (atom 0))
  (sut/click [:div#1 {:on-click (fn [_]
                                  (swap! counter inc))}] "1")
  (match @counter 1)

  (reset! counter 0)
  (sut/click [:div#1 {:on-click (fn [_] (swap! counter inc))}
              [:div#2 {:on-click (fn [_] (swap! counter inc))}]
              [:div#3 {:on-click (fn [_] (swap! counter inc))}]]
             "3")
  (match @counter 2)

  (reset! counter 0)
  (sut/click [:div#1
              {:on-click (fn [_] (swap! counter inc))}
              [:div#2
               {:on-click (fn [_] (swap! counter inc))}]
              [:div#3
               {:on-click (fn [event]
                            (swap! event assoc :propagation false)
                            (swap! counter inc))}]]
             "3")
  (match @counter 1))

(deftest fill
  (def value (atom nil))
  (sut/fill
   [:input#1 {:on-change
              (fn [event]
                (reset! value (-> @event :target :value)))}]
   "1"
   "foo")
  (match @value "foo")
  (reset! value nil)

  (sut/fill
   [:div
    [:input#1 {:on-change
               (fn [event]
                 (reset! value (-> @event :target :value)))}]]
   "1"
   "bar")
  (match @value "bar")
  (reset! value nil))

(deftest datafy
  (match
   (sut/datafy [:div {:itemProp :a} 1])
   {:a "1"})
  (match
   (sut/datafy
    [:div
     [:div {:itemProp :a} 1]
     [:div {:itemProp :b} 1]])
   {:a "1"
    :b "1"})
  (match
   (sut/datafy
    [:div {:itemScope :c}
     [:div {:itemProp :a} 1]
     [:div {:itemProp :b} 1]])
   {:c 
    {:a "1"
     :b "1"}})
  (match
   (sut/datafy
    [:div {:itemScope :c}
     [:div {:itemProp :a} 1]
     [:div {:itemProp :b} 1]
     [:div {:itemScope :d}
      (for [i [1 2 3]] ^{:key i}
        [:div {:itemProp :e} i])]]) 
   {:c {:a "1"
        :b "1"
        :d [{:e "1"}
            {:e "2"}
            {:e "3"}]}}))
