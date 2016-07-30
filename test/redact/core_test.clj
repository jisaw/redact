(ns redact.core-test
  (:require [clojure.test :refer :all]
            [redact.core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(deftest read-csv-test
  (testing "Reading in a CSV file and returning a vector of words"
    (is (= (read-csv "resources/test.csv")  ["apple" "orange" "banana" "grape" "tomatoe" "potatoe" "carrot"]))))

(deftest get-target-text-test
  (testing "Given a file name or sentance that file name or setance is returned as a single string"
    (is (= (get-target-text ["this is sentance one" "this is sentance two" "this is the last sentance three"]) (str "this is sentance one\n" "this is sentance two\n" "this is the last sentance three\n") ))
    (is (= (get-target-text ["this" "does" "not" "match" "anything,that,I,care" "about.csv"]) nil))))

(deftest gen-stoplist-test
  (testing "Given a single word, csv, or comma delimited string return a list of those stop words"
    (is (= (gen-stoplist ["this,that,the,other"]) ["this" "that"  "the" "other"]))
    (is (= (gen-stoplist ["resources/test.csv"]) ["apple" "orange" "banana" "grape" "tomatoe" "potatoe" "carrot"]))
    (is (= (gen-stoplist ["this" "twin" "sentance"] ["this" "twin" "sentance"])))))

(deftest redact-doc-test
  (testing "Given a target string and a stoplist vector return the redacted string"
    (is (= (redact-doc "this is a sample string" ["is" "string"]) "this REDACTED a sample REDACTED"))
    (is (= (redact-doc "this is another sample that has a one letter word" ["a" "letter"]) "this is another sample that has REDACTED one REDACTED word"))))
