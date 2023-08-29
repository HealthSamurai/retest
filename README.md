<p align="center">
  <img align="center" width='100' src="https://icons.iconarchive.com/icons/papirus-team/papirus-devices/256/scanner-icon.png">
</p>

<h3 align="center">
  Retest
</h3>

<p align="center">
  Library for testing <a href="https://github.com/reagent-project/reagent" target="_blank">Reagent</a> based applications
</p>

## Features

- 🖨️ Extracting component data
- 👆 Clicking on an element
- 📝 Input element filling
- 👀 Component rendering

## Installation

```edn
com.health-samurai/retest {:git/url "https://github.com/HealthSamurai/retest.git"
                           :sha "f83591af5d0f75dcdb5731dcdc59d5519b4d6c41"}
```
## Usage

### render 

```clj
(defn form-1
  [v]
  [:div v])

(defn form-2 [_]
  (fn [v]
    [form-1 v]))

(retest.core/render [form-2 1])

;; => [:div 1]
```

### datafy 
```clj

(defn component 
  []
  [:div {:data-object :a}
    [:div {:data-key :b} "1"]
    [:div {:data-array :c}
     (for [i [1 2 3]]
       [:div {:data-key :d} i])]])

(retest.core/datafy component)

;; =>
;; {:a {:b "1"
;;      :c [{:d "1"}
;;          {:d "2"}
;;          {:d "3"}]}}
```

### click
```clj
(defn component
  []
  [:form [:button {:id ::foo :on-click (fn [_] (prn "clicked"))}]])

(retest.core/click component ::foo)

;; => "clicked"
```

### fill 
```clj
(defn component
  []
  [:form
   [:input {:id ::foo
            :on-change (fn [event]
                         (prn #?(:clj  (-> @event :target :value)
                                 :cljs (.. event -target -value))))}]])

(retest.core/fill component ::foo "value")

;; => "value"
```

## References
- [Example](https://github.com/HealthSamurai/retest/tree/main/example)
  - [core_test.clj](https://github.com/HealthSamurai/retest/tree/main/example/test/example/core_test.clj)
  - [core.cljc](https://github.com/HealthSamurai/retest/blob/main/example/src/example/core.cljc)
