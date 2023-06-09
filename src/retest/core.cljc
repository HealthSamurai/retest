(ns retest.core)

(defn get-node-tag
  [node]
  (nth node 0 nil))

(defn get-node-attributes
  [node]
  (when (sequential? node)
    (let [attributes (nth node 1 nil)]
      (when (map? attributes) attributes))))

(defn sequential-node?
  [node]
  (and 
   (sequential? node)
   (:key (meta (first node)))))

(defn get-child-nodes
  [node]
  (reduce 
   (fn [acc v]
     (if (sequential-node? v) 
       (into acc v)
       (conj acc v)))
   []
   (when (sequential? node)
     (if (get-node-attributes node)
       (subvec node 2)
       (when (not= [] node)
         (subvec node 1))))))

(defn render-node
  [[component & properties]]
  (let [form (apply component properties)]
    (if (fn? form)
      (apply form properties)
      form)))

(defn render
  [node]
  (if (vector? node)
    (let [tag (get-node-tag node)]
      (if (fn? tag)
        (render (render-node node))
        (mapv render node)))
    node))

(defn get-node-id
  [node]
  (when (sequential? node)
    (let [tag (first node)]
      (or (and (keyword? tag)
               (nth (re-find #"(?:#([^\s\.#]+))" (name tag)) 1))
          (:id (get-node-attributes node))))))

(defn find-node
  [node id & [parents]]
  (if (= id (get-node-id node))
    (with-meta node {:parents parents})
    (some #(find-node % id (cons node parents))
          (get-child-nodes node))))

(defn stringify
  ([node]
   (stringify (render node) ""))
  ([node content]
   (if (or (string? node)
           (number? node))
     node
     (apply str (map #(stringify % content)
                     (get-child-nodes node))))))

(defn get-content
  [node id]
  (stringify (find-node node id) ""))

(defn click
  [node id]
  (let [element (find-node (render node) id)]
    (loop [items (cons element (:parents (meta element)))]
      (when items
        (let [attributes (get-node-attributes (first items))
              event      (atom {:propagation true})]
          (when-not (:disabled attributes)
            (when (:on-mouse-down attributes)
              ((:on-mouse-down attributes) event))
            (when (:on-mouse-up attributes)
              ((:on-mouse-up attributes) event))
            (when (:on-click attributes)
              ((:on-click attributes) event))
            (when (:propagation @event)
              (recur (next items)))))))))

(defn fill
  [node id text]
  (let [element    (find-node (render node) id)
        attributes (get-node-attributes element)
        event      (atom {:target {:value text}})]
    (when (:on-change attributes)
      ((:on-change attributes) event))))

(defn get-node-data
  [node data]
  (let [attributes (get-node-attributes node)]
    (cond
      (:itemProp attributes)
      (assoc data (:itemProp attributes) (stringify node ""))
      (:itemScope attributes)
      (assoc data (:itemScope attributes)
             (let [child (get-child-nodes node)]
               (if (:key (meta (first child)))
                 (mapv #(get-node-data % {}) (get-child-nodes node))
                 (reduce #(get-node-data %2 %1) {} (get-child-nodes node)))))
      :else (reduce #(get-node-data %2 %1) data (get-child-nodes node)))))

(defn datafy
  [node]
  (get-node-data (render node) {}))
