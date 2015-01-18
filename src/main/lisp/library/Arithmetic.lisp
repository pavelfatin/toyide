(defn inc [x] (+ x 1))

(defn dec [x] (- x 1))

(defn zero? [x] (= x 0))

(defn pos? [x] (> x 0))

(defn neg? [x] (< x 0))

(defn even? [x] (zero? (mod x 2)))

(defn odd? [x] (not (even? x)))

(defn sum [l] (reduce + 0 l))

(defn product [l] (reduce * 1 l))
