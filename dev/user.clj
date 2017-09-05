(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.reflect :refer [reflect]]
            [datasplash.api :as ds]
            [dummysplash.core :as dc])
  (:import [com.google.cloud.dataflow.sdk Pipeline$PipelineVisitor]
           [com.google.cloud.dataflow.sdk.runners TransformTreeNode]
           [com.google.cloud.dataflow.sdk.values PValue]))

(defn job [p]
  (->> p
       (ds/read-text-file "foo.txt" {:name "read"})
       (ds/map count {:name "sizes"})
       (ds/frequencies)
       (ds/write-text-file "bar.txt" {:without-sharding true})))

(defn mk-pipeline
  [& args]
  (let [p (ds/make-pipeline [])]
    (job p)
    p))

;; helper
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

(def p (mk-pipeline))

(defn get-dag
  [p]
  (let [dag (atom [])]
    (dc/visit p
      :enter (fn [n]
               (println "enter"))
      :leave (fn [n]
               (println "leave"))
      :visit (fn [n]
               (let [node-name (first (dc/node-enclosings n))]
                 (swap! dag (fn [ls]
                             (let [lst (last ls)]
                               (if-not (= lst node-name)
                                 (conj ls node-name)
                                 ls))))))
      :value (fn [v n]
               (println "value")))
    @dag))
