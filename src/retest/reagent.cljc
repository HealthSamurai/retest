(ns retest.reagent
  (:require [retest.hiccup :as hiccup]))

(defn render-node
  [[component & properties]]
  (let [form (apply component properties)]
    (if (fn? form)
      (apply form properties)
      form)))

(defn render
  [node]
  (cond
    (sequential? node)
    (let [tag (hiccup/get-tag node)]
      (if (or (fn? tag) (var? tag))
        (render (render-node node))
        (reduce (fn [acc v]
                  (if (hiccup/sequential-node? v)
                    (into acc (map render v))
                    (conj acc (render v))))
                [] node)))
    (or (fn? node) (var? node))
    (render (node))
    :else node))

(defn click
  [node]
  (loop [[item & items] (cons node (:parents (meta node)))]
    (let [attributes (hiccup/get-attributes item)
          event      (atom {:propagation true})]
      (when-not (:disabled attributes)
        (doseq [event-key [:on-mouse-down
                           :on-mouse-up
                           :on-click
                           :href]]
          (when-let [event-fn (get attributes event-key)]
            (event-fn event)))
        (when (and (:propagation @event) items)
          (recur items))))) )

(defn fill
  [node text]
  (let [attributes (hiccup/get-attributes node)
        event      (atom {:target {:value text}})]
    (when-not (:disabled attributes)
      (when (:on-change attributes)
        ((:on-change attributes) event)))))
