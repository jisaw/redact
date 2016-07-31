(ns redact.core-test
  (:require [clojure.test :refer :all]
            [redact.core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(deftest read-csv-test
  (testing "Reading in a CSV file and returning a vector of words"
    (is (= (read-csv "resources/test.csv")  ["apple" "orange" "banana" "grape" "tomatoe" "potatoe" "carrot"]))))

(deftest redact-doc-test
  (testing "Given a target string and a stoplist vector return the redacted string"
    (is (= (redact-doc "this is a sample string" ["is" "string"]) "this REDACTED a sample REDACTED"))
    (is (= (redact-doc "this is another sample that has a one letter word" ["a" "letter"]) "this is another sample that has REDACTED one REDACTED word"))))

(deftest add-arg-to-map-test
  (testing "Pass a string and a map and get a map containing that string back"
    (is (= (add-arg-to-map "this" {:target "" :stoplist '()}) {:target "" :stoplist '("this")}))
    (is (= (add-arg-to-map "one,two,three" {:target "" :stoplist '()}) {:target "" :stoplist '("three" "two" "one")}))
    (is (= (add-arg-to-map "this is an example sentance" {:target "" :stoplist '()}) {:target "this is an example sentance" :stoplist '()}))
    (is (= (add-arg-to-map "resources/test.txt" {:target "" :stoplist '()})) {:target "This is a text file\nIt has a couple different things in it\nThat are all on different lines\n" :stoplist '()})
    (is (= (add-arg-to-map "resources/test.csv" {:target "" :stoplist '()})) {:target "" :stoplist '("apple" "orange" "banana" "grape" "tomatoe" "potatoe" "carrot")}))
  )
