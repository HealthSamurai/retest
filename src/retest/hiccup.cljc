(ns retest.hiccup)

(def tag-pattern #"(?:#([^\s\.#]+))")

(defn get-tag
  [node]
  (when (vector? node)
    (nth node 0 nil)))

(defn get-attributes
  [node]
  (when (vector? node)
    (let [attributes (nth node 1 nil)]
      (when (map? attributes)
        attributes))))

(defn get-id
  [node]
  (or (:id (get-attributes node))
      (when-let [tag (get-tag node)]
        (nth (re-find tag-pattern (name tag)) 1 nil))))

(defn sequential-node?
  [node]
  (and
   (sequential? node)
   (let [element (first node)]
     (or (:key (meta element))
         (:key (get-attributes element))))))

(defn stringify
  [node]
  (cond
    (string? node)     node
    (number? node)     (str node)
    (sequential? node) (apply str (map stringify node))))

(defn get-children
  [node]
  (when (vector? node)
    (if (get-attributes node)
      (subvec node 2)
      (subvec node 1))))

(defn datafy
  [node & [data]]
  (let [attributes (get-attributes node)]
    (cond
      (:data-key attributes)
      (assoc data (:data-key attributes) (stringify node))
      (:data-array attributes)
      (assoc data (:data-array attributes) (mapv datafy (get-children node)))
      (:data-object attributes)
      (assoc data (:data-object attributes) (reduce #(datafy %2 %1) {} (get-children node)))
      :else (reduce #(datafy %2 %1) data (get-children node)))))

(defn find-node
  [node id & [parents]]
  (if (= id (get-id node))
    (with-meta node {:parents parents})
    (some #(find-node % id (cons node parents))
          (get-children node))))
