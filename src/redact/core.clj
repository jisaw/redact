(ns redact.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
))

(defn redact-doc
  ;; Reads the file line by line and redacts all the matched words
  ([target stoplist]
   (if (empty? stoplist)
     (println target)
     (redact-doc (str/replace target (re-pattern (first stoplist)) "REDACTED") (rest stoplist))))
  )

(defn get-target-text
  ;; Takes a vector of args and returns a String of a text file
  ([args]
   (if (empty? args)
     (println "No text file was found!")
     (if (.contains (first args) ".txt")
       (str (slurp (first args)))
       (get-target-text (rest args)))))
  ;; TODO: Change to regex match for mutliple text based file types
  
 )

(defn read-csv
  ;; Takes in a filename and returns a vector of the csv values
  [file-name]
  (str/split (with-open [rdr (io/reader file-name)]
     (doall (reduce str (line-seq rdr)))) #","))

(defn gen-stoplist
  ;; Generates the stoplist for words to be redacted
  ([args] (gen-stoplist args []))
  ([args stoplist]
   (if (empty? args)
     stoplist
     (gen-stoplist (rest args) (if (.contains (first args) ".csv")
                                 (into [] (concat stoplist (read-csv (first args))))
                                 (if (.contains (first args) ".txt")
                                   stoplist
                                   (into [] (concat stoplist [(first args)] ))))))))

(defn -main
  [& args]
  (redact-doc (get-target-text args) (gen-stoplist args))
)
