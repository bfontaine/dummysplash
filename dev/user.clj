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
