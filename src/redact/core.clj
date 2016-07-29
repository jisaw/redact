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
     (redact-doc (str/replace target (re-pattern (str "\\s(" (first stoplist) ")\\s")) " REDACTED ") (rest stoplist))))
  )

(defn get-target-text
  ;; Takes a vector of args and returns a String of a text file
  ([args] (get-target-text args ""))
  ([args result]
   (if (empty? args)
     result
     (get-target-text (rest args) (if (boolean (re-find #"(.+\.[^csv\s])" (first args)))
                                    (str result (slurp (first args)))
                                    (if (not (boolean (re-find #"(.+\.csv|.+,.+)" (first args))))
                                      (if (boolean (re-find #"\s" (str/trim (first args))))
                                        (str result (first args))))))))
  
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
     (gen-stoplist (rest args) (if (boolean (re-find #"(.+\.csv)" (first args)))
                                 (into [] (concat stoplist (read-csv (first args))))
                                 (if (boolean (re-find #"(.+\..[^csv\s])" (first args)))
                                   stoplist
                                   (if (boolean (re-find #"(.*,.*)" (first args))) 
                                     (into [] (concat stoplist (str/split (first args) #",")))
                                     (if (boolean (re-find #"(\s)" (str/trim (first args))))
                                       stoplist
                                       (into [] (concat stoplist [(first args)] ))))))))))

(defn -main
  [& args]
  (redact-doc (get-target-text args) (gen-stoplist args))
)
