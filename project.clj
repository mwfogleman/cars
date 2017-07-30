(defproject cars "0.1.0-SNAPSHOT"
  :description "A way to explore the NHTSA API"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.7.1"]
                 [clj-http "3.6.1"]
                 [com.rpl/specter "1.0.2"]
                 [org.clojure/clojure "1.9.0-alpha16"]]
  :main ^:skip-aot cars.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
