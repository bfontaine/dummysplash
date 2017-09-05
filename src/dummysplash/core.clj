(ns dummysplash.core
  (:require [datasplash.api :as ds]))

(defn fmt-freq
  [[k v]]
  (format "%s: %d" k v))

(defn job [p]
  (->> p
       (ds/read-text-file "foo.txt" {:name "read"})
       (ds/map count {:name "sizes"})
       (ds/frequencies)
     ; (ds/map fmt-freq {:name "format"})
       (ds/write-text-file "bar.txt" {:without-sharding true})))

(defn mk-pipeline
  [& args]
  (let [p (ds/make-pipeline [])]
    (job p)
    p))
