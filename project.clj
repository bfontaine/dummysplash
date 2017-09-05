(defproject dummysplash "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/bfontaine/dummysplash"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [datasplash          "0.4.1"]]

  :profiles {:dev {:repl-options {:init-ns user}
                   :source-paths ["src" "dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]]}})
