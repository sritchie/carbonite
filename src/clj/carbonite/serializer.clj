(ns carbonite.serializer
  (:require [clojure.string :as s])
  (:import [carbonite ClojureMapSerializer RatioSerializer
            ClojureReaderSerializer PrintDupSerializer StringSeqSerializer
            ClojureVecSerializer ClojureSetSerializer ClojureSeqSerializer]
           [com.twitter.chill.java  RegexSerializer SqlDateSerializer
            SqlTimeSerializer TimestampSerializer URISerializer UUIDSerializer]
           [com.esotericsoftware.kryo Kryo]
           [com.esotericsoftware.kryo.io Input Output]
           [java.util UUID]
           [java.util.regex Pattern]
           [java.sql Time Timestamp]
           [clojure.lang Keyword Symbol PersistentArrayMap
            PersistentHashMap MapEntry PersistentStructMap
            PersistentVector PersistentHashSet Ratio ArraySeq
            Cons PersistentList PersistentList$EmptyList Var
            LazySeq IteratorSeq StringSeq PersistentVector$ChunkedSeq]))

(defn clj-print
  "Use the Clojure pr-str to print an object into the Output using
  pr-str."
  [^Output output obj]
  (.writeString output (pr-str obj)))

(defn clj-print-dup
  "Use the Clojure pr-str to print an object into the buffer using
   pr-str w/ *print-dup* bound to true."
  [output obj]
  (binding [*print-dup* true]
    (clj-print output obj)))

(defn clj-read
  "Use the Clojure read-string to read an object from a buffer."
  [^Input input]
  (read-string (.readString input)))

(defn print-collection
  [^Kryo registry ^Output output coll]
  (.writeInt output (count coll) true)
  (doseq [x coll]
    (.writeClassAndObject registry output x)))

(defn read-seq
  [^Kryo registry ^Input input]
  (let [len (.readInt input true)]
    (->> (repeatedly len #(.readClassAndObject registry input))
         (apply list))))

(defn mk-collection-reader [init-coll]
  ;; TODO: Accept Kryo and Input
  (fn [^Kryo registry ^Input input]
    (loop [remaining (.readInt input true)
           data      (transient init-coll)]
      (if (zero? remaining)
        (persistent! data)
        (recur (dec remaining)
               (conj! data (.readClassAndObject registry input)))))))

(def read-vector (mk-collection-reader []))
(def read-set    (mk-collection-reader #{}))

(defn write-map
  "Write an associative data structure to Kryo's buffer. Write entry
   count as an int, then serialize alternating key/value pairs."
  [^Kryo registry ^Output output m]
  (.writeInt output (count m) true)
  (doseq [[k v] m]
    (.writeClassAndObject registry output k)
    (.writeClassAndObject registry output v)))

(defn read-map
  "Read a map from Kryo's buffer.  Read entry count, then deserialize alternating
   key/value pairs.  Transients are used for performance."
  [^Kryo registry ^Input input]
  (doall
   (loop [remaining (.readInt input true)
          data      (transient {})]
     (if (zero? remaining)
       (persistent! data)
       (recur (dec remaining)
              (assoc! data
                      (.readClassAndObject registry input)
                      (.readClassAndObject registry input)))))))

(defn write-string-seq [^Output output string-seq]
  (.writeString output (s/join string-seq)))

(defn read-string-seq [^Input input]
  (seq (.readString input)))

(def ^{:doc "Define a map of Clojure primitives and their serializers
  to install."}
  clojure-primitives
  (let [prims (array-map
               Keyword (ClojureReaderSerializer.)
               Symbol  (ClojureReaderSerializer.)
               Ratio   (RatioSerializer.)
               Var     (PrintDupSerializer.))]
    (if-let [big-int (try (Class/forName "clojure.lang.BigInt")
                          (catch ClassNotFoundException _))]
      (assoc prims big-int (ClojureReaderSerializer.))
      prims)))

(def java-primitives
  (array-map
   Timestamp     (TimestampSerializer.)
   java.sql.Date (SqlDateSerializer.)
   java.sql.Time (SqlTimeSerializer.)
   java.net.URI  (URISerializer.)
   Pattern       (RegexSerializer.)
   UUID          (UUIDSerializer.)))

(def clojure-collections
  (concat
   ;; collections where we can use transients for perf
   [[PersistentVector (ClojureVecSerializer.)]
    [PersistentHashSet (ClojureSetSerializer.)]
    [MapEntry (ClojureVecSerializer.)]]

   ;; list/seq collections
   (map #(vector % (ClojureSeqSerializer.))
        [Cons PersistentList$EmptyList PersistentList
         LazySeq IteratorSeq ArraySeq
         PersistentVector$ChunkedSeq])

   ;; other seqs
   [[StringSeq (StringSeqSerializer.)]]

   ;; maps - use transients for perf
   (map #(vector % (ClojureMapSerializer.))
        [PersistentArrayMap PersistentHashMap PersistentStructMap])))

;; Copyright 2011 Revelytix, Inc.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;;     http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.
