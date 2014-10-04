(ns laptops.k-nearest-neighbors)

 (defn square [x]
  (* x x))

(defn euclidean-distance
  "Returns the euclidean distance between points x and y
   where x and y are vectors"
  [p q]
  (Math/sqrt
    (->> (map - p q)
         (map square)
         (reduce +))))

(def distance-fn (fn [v1 v2] (euclidean-distance v1 v2)))

(defn distance
  "Calculate the Euclidean distance.
   Take the distance in each axis, square them and add them together"
  [x y]
  (let [[a b] x
        [c d] y]
    (Math/sqrt
      (+ (square (- a c)) (square (- b d))))))

(defn distance-normalized
  "A function that gives higher values for items that are similar"
  [x y]
 ; (/ 1 (+ 1 
          (euclidean-distance x y)
       ;   ))
  )

(defn majority-label
  "return the majority-label from the data set.
   this isn't the cleanest/best way to do this I'm sure
   but works for now"
  [results]
  (let [labels (map first results)]
    (println "labele: " labels)
    (println "broj labela: " (count labels))
;  (println "prosek je:" 
          (double (/ (reduce + labels) (count labels)))
         ;  )
   ; (first
    ;  (->> labels
          ; (partition-by identity)
          ; (sort-by count)
          ; (reverse)
     ;      (flatten)
      ;     ))
    
    ))

(defn knn
  "return the k nearest neighbors"
  [sample data k]
  (let [distance-fn (partial distance-normalized sample)
        results (map #(vector (first %) (distance-fn (second %))) data)]
    (println "rezultat: " results)
    (take k (sort-by second  < results))))

(defn classify
  "Use KNN to find similarity in data"
  [sample data k]
  (let [kn (knn sample data k)]
    (majority-label kn)))