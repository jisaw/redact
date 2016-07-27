(ns redact.core
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))

(defn -main
  [& args]
  (def stoplist [])
  (map #(dispatch %) args))

(defn read-csv
  ;; Takes in a filename and returns a vector of the csv values
  [file-name]
  (str/split (with-open [rdr (io/reader file-name)]
     (doall (reduce str (line-seq rdr)))) #","))


(defn dispatch
  ;; Checks if the object is this or that and sends it to the right place
  [element]
  (if (.contains element ".txt")
    (def text-file element))
  (if (.contains element ".csv")
    (try 
      (def stoplist (distinct (concat stoplist (read-csv element))))
      (catch Exception e (println "Exception caught: " (.getMessage e))))
    (def stoplist (distinct (conj stoplist element))))
  )
