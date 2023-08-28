(ns example.core
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::set-name
 (fn [db [_ value]]
   (assoc db :name-value value)))

(rf/reg-event-db
 ::submit
 (fn [db _]
   (assoc db :name (:name-value db))))


(rf/reg-sub
 ::get-name
 (fn [db _]
   (or (:name db) "unknown")))

(defn input
  []
  [:input
   {:id ::name-input
    :on-change
    (fn [e]
      (let [value #?(:cljs (.. e -target -value)
                     :clj  (->> @e :target :value))]
        (rf/dispatch-sync [::set-name value])))}])

(defn button
  []
  [:button
   {:id ::submit
    :on-click (fn [_] (rf/dispatch-sync [::submit]))} "Submit"])

(defn yourname-display
  []
  (let [yourname (rf/subscribe [::get-name])]
    [:div {:data-object ::info}
     [:b {:data-key ::name} @yourname]]))

(defn index
  []
  [:div
   [yourname-display]
   [input]
   [button]])
