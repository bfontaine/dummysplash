(ns dummysplash.core
  (:import [com.google.cloud.dataflow.sdk Pipeline$PipelineVisitor]
           [com.google.cloud.dataflow.sdk.runners TransformTreeNode]
           [com.google.cloud.dataflow.sdk.values PValue]))

(defn node-enclosings
  [^TransformTreeNode n]
  (loop [n n
         acc []]
    (let [s (.getFullName n)]
      (if (empty? s)
        acc
        (recur (.getEnclosingNode n)
               (cons s acc))))))

(defn mk-visitor*
  [args]
  (let [fns (into {} (map vec (partition-all 2 args)))

        enter (get fns :enter identity)
        leave (get fns :leave identity)
        visit (get fns :visit identity)
        value (get fns :value identity)]
    (reify
      Pipeline$PipelineVisitor
      (^void enterCompositeTransform
        [this ^TransformTreeNode n]
        (enter n))
      (^void leaveCompositeTransform
        [this ^TransformTreeNode n]
        (leave n))
      (^void visitTransform
        [this ^TransformTreeNode n]
        (visit n))
      (^void visitValue
        [this ^PValue v ^TransformTreeNode n]
        (value v n)))))

(defn mk-visitor
  [& args]
  (mk-visitor* args))

(defn visit
  ([p] (visit (mk-visitor* [])))
  ([p v]
   (.traverseTopologically p v))
  ([p x y & args]
   (visit p (mk-visitor* (cons x (cons y args))))))

(defn get-dag
  "Return a very simple version of a flattened DAG from the given pipeline"
  [p]
  (let [dag (atom [])]
    (visit p
      :enter (fn [n]
               ;(println "enter")
               )
      :leave (fn [n]
               ;(println "leave")
               )
      :visit (fn [n]
               (let [node-name (first (node-enclosings n))]
                 (swap! dag (fn [ls]
                             (let [lst (last ls)]
                               (if-not (= lst node-name)
                                 (conj ls node-name)
                                 ls))))))
      :value (fn [v n]
               ;(println "value")
               ))
    @dag))
