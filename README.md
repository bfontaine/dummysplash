# Dummysplash

## Example

```clojure
(require '[dummysplash.core :as dc])

(defn job [p]
  (->> p
       (ds/read-text-file "foo.txt" {:name "read"})
       (ds/map count {:name "sizes"})
       (ds/frequencies)
       (ds/write-text-file "bar.txt" {:without-sharding true})))

(defn mk-pipeline
  []
  (let [p (ds/make-pipeline [])]
    (job p)
    p))

(dc/get-dag (mk-pipeline))
;; => ["read" "sizes" "Count.PerElement" "TextIO.Write"]
```

## License

Copyright © 2017 Baptiste Fontaine

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
