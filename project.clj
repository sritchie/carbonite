(def shared '[[com.esotericsoftware.kryo/kryo "2.17"]
              [com.twitter/meat-locker "0.3.1"]])

(defproject com.twitter/carbonite "1.3.1"
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :description "Write Clojure data to and from bytes using Kryo."
  :repositories {"conjars" "http://conjars.org/repo/"}
  :dev-dependencies [[lein-multi "1.1.0-SNAPSHOT"]]
  :dependencies      ~(conj shared '[org.clojure/clojure "1.4.0"])
  :multi-deps {"1.2" ~(conj shared '[org.clojure/clojure "1.2.1"])
               "1.3" ~(conj shared '[org.clojure/clojure "1.3.0"])}
  :warn-on-reflection true)
