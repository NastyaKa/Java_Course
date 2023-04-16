;checks
(defn same-v [args]
    (and
     (every? vector? args)
     (every? (partial == (count (first args))) (mapv count args))))

(defn same-m [& m] (same-v (mapv first m)))
(defn same-t [& t] (== (mapv count t)))

(defn vect? [v]
  (and
   (vector? v)
   (every? number? v)))

(defn matr? [m]
  (same-v m))

(defn tens? [t]
  (or (number? t) (and (or (every? number? t) (apply same-t t))
      (vector? t) (every? tens? t))))

(defn pref? [t] (every? identity (apply mapv = t)))

;creators
(defn creator [f check-f]
  (fn [& args]
    {:pre [(or (every? number? args) (same-v args)) (every? check-f args)]}
    (apply mapv f args)))

(defn shape [t]
  {:pre (tens? t)}
  (if (number? t) []
    (conj (shape (first t)) (count t))))

(defn cast-shape [t dims]
  (reduce (fn [t dim] (vec (repeat dim t)))
          t dims))

(defn broadcast [t]
  (letfn [(pref-cast [t vector]
            (let [max-vector (apply (fn max-cast [& t]
                                      {:pre [(every? tens? t)]}
                                        (reduce (fn [a, b]
                                          (if (>= (count a) (count b)) a b)) t)) vector)]
              (mapv cast-shape t (mapv #(subvec max-vector (count %)) vector))))]
    (pref-cast t (mapv shape t))))

(defn creator-t [f]
  (fn [& args]
    {:pre [(every? tens? args) (pref? (mapv shape args))]}
    (letfn [(t-func [& tensors] (if (number? (first tensors)) (apply f tensors) (apply mapv t-func tensors)))]
      (apply t-func (broadcast args)))))

;main
(def v+ (creator + vect?))
(def v- (creator - vect?))
(def v* (creator * vect?))
(def vd (creator / vect?))

(defn v*s [v & args]
  {:pre [(vect? v) (every? number? args)]
   :post [(vect? %)]}
    (mapv (partial * (apply * args)) v))

(defn scalar [& args]
  {:pre [(every? vect? args) (same-v args)]
   :post [(number? %)]}
    (apply + (apply v* args)))

(defn vect [& args]
  {:pre [(every? vect? args) (every? (fn [x] (== (count x) 3)) args)]
   :post [(vect? %) (== 3 (count %))]}
    (reduce (fn [a b] (vector (- (* (nth a 1) (last b)) (* (last a) (nth b 1)))
                             (- (* (last a) (first b)) (* (first a) (last b)))
                             (- (* (first a) (nth b 1)) (* (nth a 1) (first b))))) args))

(def m+ (creator v+ matr?))
(def m- (creator v- matr?))
(def m* (creator v* matr?))
(def md (creator vd matr?))

(defn m*s [m & args]
  {:pre [(matr? m)]
   :post [(matr? %) (same-m % m)]}
    (mapv (fn [v] (apply (partial v*s v) args)) m))

(defn m*v [m & v]
  {:pre  [(matr? m) (every? vect? v)]}
  (mapv (fn [a] (apply scalar a v)) m))

(defn transpose [m]
  {:pre [(matr? m)]}
    (apply mapv vector m))

(defn m*m [& args]
  {:pre [(every? matr? args)]
   :post [(matr? %) (== (count %) (count (first args))) (= (count (first %)) (count (first (last args))))]}
    (reduce (fn [a b] (mapv (partial m*v (transpose b)) a)) args))

(def hb+ (creator-t +))
(def hb- (creator-t -))
(def hb* (creator-t *))
(def hbd (creator-t /))
