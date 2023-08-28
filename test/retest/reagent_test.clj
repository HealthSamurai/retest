(ns retest.reagent-test
  (:require
   [retest.reagent :as sut]
   [clojure.test  :refer [deftest testing]]
   [matcho.core   :refer [match]]))

(defn form-1
  [v]
  [:div v])

(defn form-2 [_]
  (fn [v]
    [form-1 v]))

(defn form-1-collection
  [coll]
  [:div
   [form-1 coll]
   (for [i coll] ^{:key i}
     [form-1 i])])

(defn form-2-collection
  [coll]
  [:div
   [form-2 coll]
   (for [i coll] ^{:key i}
     [form-2 i])])

(deftest render-test
  (testing "form-1"
    (match (sut/render [form-1 1])   [:div 1])
    (match (sut/render [#'form-1 1]) [:div 1]))
  (testing "form-2"
    (match (sut/render [form-2 1])   [:div 1])
    (match (sut/render [#'form-2 1]) [:div 1]))
  (testing "form-1 collection"
    (match
      (sut/render [form-1-collection [1 2]])
      [:div [:div [1 2]] [:div 1] [:div 2]]))
  (testing "form-2 collection"
    (match
      (sut/render [form-2-collection [1 2]])
      [:div [:div [1 2]] [:div 1] [:div 2]])))

(deftest click
  (def counter (atom 0))
  (sut/click [:div#1 {:on-click (fn [_] (swap! counter inc))}])
  (match @counter 1)

  (reset! counter 0)
  (sut/click (with-meta [:div#3 {:on-click (fn [_] (swap! counter inc))}]
               {:parents [[:div#2 {:on-click (fn [_] (swap! counter inc))}]
                          [:div#1 {:on-click (fn [_] (swap! counter inc))}]]}))
  (match @counter 3)

  (reset! counter 0)
  (sut/click (with-meta
                [:div#3
                 {:on-click (fn [event]
                              (swap! event assoc :propagation false)
                              (swap! counter inc))}]
                {:parents [[:div#2 {:on-click (fn [_] (swap! counter inc))}]
                           [:div#1 {:on-click (fn [_] (swap! counter inc))}]]}))
  (match @counter 1))

(deftest fill
  (def value (atom nil))
  (sut/fill
    [:input {:on-change
             (fn [event]
               (reset! value (-> @event :target :value)))}]
   "foo")
  (match @value "foo")

  (reset! value nil)
  (sut/fill
    [:input {:disabled true
             :on-change
             (fn [event]
               (reset! value (-> @event :target :value)))}]
   "bar")
  (match @value nil))
