(ns redact.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            ))

(defn read-csv
  ;; Takes in a filename and returns a vector of the csv values
  [file-name]
  (str/split (with-open [rdr (io/reader file-name)]
               (doall (reduce str (line-seq rdr)))) #","))


(defn add-arg-to-map
  ;; Takes in an arg and a map and return an updated map containing that arg
  [arg args-map]
  (if (boolean (re-find #"(.+\.[^csv\s])" arg))
    (assoc args-map :target (str/trim (str/join " " [(:target args-map) (slurp arg)])))
    (if (boolean (re-find  #"(.+\.csv)" arg))
      (assoc args-map :stoplist (concat (:stoplist args-map) (into '() (read-csv arg)))) ;;handle CSV
      (if (boolean (re-find #"(.*,.*)" arg))
        (assoc args-map :stoplist (concat (:stoplist args-map) (into '() (str/split arg #",")))) ;;handle comma seperated string
        (if (boolean (re-find #"\s" (str/trim arg)))
          (assoc args-map :target (str/trim (str/join " " [(:target args-map) arg]))) ;;handle a full sentance
          (assoc args-map :stoplist (conj (:stoplist args-map) arg)) ;; handle single word
          )))))

(defn populate-map
  ;; Takes in an args list and return a map - using tail recursion
  ([args] (populate-map args {:target "" :stoplist '()}) )
  ([args result-map]
   (if (empty? args)
     (assoc result-map :target (str/trim (:target result-map)))
     (populate-map (rest args) (add-arg-to-map (first args) result-map))))
  )

(defn redact-doc
  ;; Reads the file line by line and redacts all the matched words
  ([target stoplist]
   (if (empty? stoplist)
     (str/trim target)
     (redact-doc (str/replace target (re-pattern (str "\\s(" (first stoplist) ")(\\s|$)")) " REDACTED ") (rest stoplist))))
  )

(defn -main
  [& args]
  (let [run-map (populate-map (first args))]
    (println (redact-doc (:target run-map) (:stoplist run-map))))
)
