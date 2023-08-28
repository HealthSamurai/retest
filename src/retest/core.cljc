(ns retest.core
  (:refer-clojure :exclude [find])
  (:require retest.hiccup
            retest.reagent))

(defn render
  [root]
  (retest.reagent/render root))

(defn find
  [root id]
  (->
    (retest.reagent/render root)
    (retest.hiccup/find-node id)))

(defn click
  ([root]
   (-> (render root)
       (retest.reagent/click)))
  ([root id]
   (-> (find root id)
       (retest.reagent/click))))

(defn fill
  ([root text]
   (-> (render root)
       (retest.reagent/fill text)))
  ([root id text]
   (-> (find root id)
       (retest.reagent/fill text))))

(defn stringify
  ([root]
   (-> (render root)
       (retest.hiccup/stringify)))
  ([root id]
   (-> (find root id)
       (retest.hiccup/stringify))))

(defn datafy
  ([root]
   (-> (render root)
       (retest.hiccup/datafy)))
  ([root id]
   (-> (find root id)
       (retest.hiccup/datafy))))
