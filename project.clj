(defproject com.twitter/carbonite "1.4.0"
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :javac-options ["-source" "1.6" "-target" "1.6"]
  :description "Write Clojure data to and from bytes using Kryo."
  :repositories {"conjars" "http://conjars.org/repo/"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.esotericsoftware.kryo/kryo "2.21"]
                 [com.twitter/chill-java "0.3.5"]]
  :global-vars {*warn-on-reflection* true}
  :profiles {:1.2 {:dependencies [[org.clojure/clojure "1.2.1"]]}
             :1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}})
