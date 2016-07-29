(ns redact.core-test
  (:require [clojure.test :refer :all]
            [redact.core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(deftest read-csv-test
  (testing "Reading in a CSV file and returning a vector of words"
    (is (= (read-csv "resources/test.csv")  ["apple" "orange" "banana" "grape" "tomatoe" "potatoe" "carrot"]))))

(deftest get-target-text-test
  (testing "Given a file name or sentance that file name or sentance is combined and returned"
    (is (= (get-target-text ["this is sentance one" "this is sentance two" "this is the last sentance three"]) (str "this is sentance one\n" "this is sentance two\n" "this is the last sentance three\n") ))
    (is (= (get-target-text ["this" "does" "not" "match" "anything,that,I,care" "about.csv"]) nil))))
