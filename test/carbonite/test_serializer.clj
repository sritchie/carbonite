(ns carbonite.test-serializer
  (:use [clojure.test]
        [carbonite api buffer serializer])
  (:import [com.esotericsoftware.kryo Serializer]
           [java.nio ByteBuffer]))

;; An elegant weapon, not as clumsy or random as a blaster.
(defrecord LightSaber [style color])

;; Create a custom serializer for the two fields of LightSaber
(def saber-serializer
  (proxy [Serializer] []
    (write [kryo output saber]
      (clj-print output (:style saber))
      (clj-print output (:color saber)))
    (read [kryo input type]
      (LightSaber. (clj-read input)
                   (clj-read input)))))

(deftest test-custom-serializer
  (let [registry   (doto (default-registry)
                     (register-serializers {LightSaber saber-serializer}))
        darth-maul (LightSaber. :double-bladed :red)]
    (is (= darth-maul
           (->> darth-maul
                (write-bytes registry)
                (read-bytes registry))))))
