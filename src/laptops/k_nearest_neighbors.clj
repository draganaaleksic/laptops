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

(defn average-price
  "Return the average price based on k nearest neighbours"
  [results]
  (let [prices (map first results)]
  (println "labele: " prices) 
  (double (/ (reduce + prices) (count prices)))))

(defn knn
  "Return the k nearest neighbors"
  [sample data k]
  (let [distance-fn (partial euclidean-distance sample)
        results (map #(vector (first %) (distance-fn (second %))) data)]
    (println "rezultat: " results)
    (take k (sort-by second  < results))))

(defn classify
  "Use KNN to find similarity in data"
  [sample data k]
  (let [kn (knn sample data k)]
    (average-price kn)))