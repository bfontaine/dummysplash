(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.reflect :refer [reflect]]
            [dummysplash.core :as dc])
  (:import [com.google.cloud.dataflow.sdk Pipeline$PipelineVisitor]
           [com.google.cloud.dataflow.sdk.runners TransformTreeNode]
           [com.google.cloud.dataflow.sdk.values PValue]))

(defn r
  [x]
  (->> x
       reflect
       :members
       (filter (fn [member]
                 ((:flags member) :public)))
       (map #(dissoc % :flags
                       :exception-types
                       :declaring-class))))

(defn mk-visitor
  [& args]
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

(defn enclosings
  [n]
  (loop [n n
         acc []]
    (let [s (.getFullName n)]
      (if (empty? s)
        acc
        (recur (.getEnclosingNode n)
               (cons s acc))))))

(def p (dc/mk-pipeline))

(def dag (atom []))

(def v (mk-visitor
         :enter (fn [n]
                  (println "enter"))
         :leave (fn [n]
                  (println "leave"))
         :visit (fn [n]
                  (let [node-name (first (enclosings n))]
                    (swap! dag (fn [ls]
                                (let [lst (last ls)]
                                  (if-not (= lst node-name)
                                    (conj ls node-name)
                                    ls))))))
         :value (fn [v n]
                  (println "value"))))

(defn visit
  [v p]
  (.traverseTopologically p v))

;; user=> (visit v p)
;; user=> (enclosings toto)
;; user-> @dag
