(def nil (list))

(defn empty? [l] (if l false true))

(defn not-empty? [l] (not (empty? l)))

(defn second [l] (first (rest l)))

(defn count [l]
  (loop [l' l, c 0]
    (if l' (recur (rest l') (inc c)) c)))

(defn repeat [n x]
  (loop [i n, l nil]
    (if (pos? i)
        (recur (dec i) (cons x l))
        l)))

(defn range [a b]
  (loop [i (dec b), l nil]
    (if (>= i a)
        (recur (dec i) (cons i l))
        l)))

(defn nth [l n]
  (if (zero? n)
      (first l)
      (recur (rest l) (dec n))))

(defn every? [f l]
  (if-let [[h & t] l]
    (if (f h)
        (recur f t)
        false)
    true))

(defn some [f l]
  (if-let [[h & t] l]
    (if-let [v (f h)]
      v
      (recur f t))
    nil))

(defn fold-left [f z l]
  (if-let [[h & t] l]
    (recur f (f z h) t)
    z))

(defn fold-right [f z l]
  (if-let [[h & t] l]
    (f h (fold-right f z t))
     z))

(defn reduce-left [f l]
  (if-let [[h & t] l]
    (fold-left f h t)
    (error "reduce on empty list")))

(defn reduce-right [f l]
  (reduce-left (flip f) (reverse l)))

(defn reverse [l]
  (fold-left (flip cons) nil l))

(defn concat [& ls]
  (fold-left #(fold-left (flip cons) %2 (reverse %1)) nil ls))

(defn map [f l]
  (reverse (fold-left #(cons (f %2) %1) nil l)))

(defn map-all [f & ls]
  (if (every? not-empty? ls)
    (let [hs (map first ls) ts (map rest ls)]
      (cons (apply f hs) (apply map-all (cons f ts))))
    nil))

(defn mapcat [f & ls]
  (apply concat (apply (partial map-all f) ls)))

(defn-poly reduce
  ([f l] (reduce-left f l))
  ([f z l] (fold-left f z l)))

(defn flatten [l]
  (let [f (fn [x acc]
            (if (list? x)
                (concat (flatten x) acc)
                (cons x acc)))]
    (fold-right f nil l)))

(defn filter [f l]
  (reverse
    (let [g (fn [acc x]
              (if (f x)
                  (cons x (filter f acc))
                  (filter f acc)))]
      (reduce g nil l))))

(defn separate [f l]
  (list (filter f l) (filter (complement f) l)))

(defn last [l]
   (if l
       (reduce (fn [_ x] x) l)
       (error "last on empty list")))

(defn take [n l]
  (if (and (pos? n) (not-empty? l))
      (cons (first l) (take (dec n) (rest l)))))

(defn drop [n l]
  (if (and (pos? n) (not-empty? l))
      (drop (dec n) (rest l))
      l))

(defn take-last [n l]
  (drop (- (count l) n) l))

(defn drop-last [n l]
  (take (- (count l) n) l))

(defn split-at [n l]
  (if (and (pos? n) (not-empty? l))
      (let [[l1 l2] (split-at (dec n) (rest l))]
        (list (cons (first l) l1) l2))
      (list nil l)))

(defn partition [n l]
  (if l
      (let [[l1 l2] (split-at n l)]
        (cons l1 (partition n l2)))))

(defn take-while [f l]
  (if-let [[h & t] l]
    (if (f h)
        (cons h (take-while f t)))))

(defn drop-while [f l]
  (if-let [[h & t] l]
    (if (f h)
        (recur f t)
        l)))

(defn split-with [f l]
  (list (take-while f l) (drop-while f l)))

(defn partition-by [f l]
  (if l
      (let [x (f (first l)),
            [a b] (split-with #(= (f %) x) l)]
        (cons a (partition-by f b)))))

(defn zip [l1 l2]
  (if (and (not-empty? l1) (not-empty? l2))
      (let [[h1 & t1] l1, [h2 & t2] l2]
        (cons (list h1 h2) (zip t1 t2)))))

(defn zip-all [& ls]
  (apply map-all (cons list ls)))

(defn unzip [l]
  (fold-right (fn [[x1 x2] [l1 l2]]
                (list (cons x1 l1) (cons x2 l2)))
              (list nil nil)
              l))

(defn first-index-of [f l]
  (loop [l' l, i 0]
    (if-let ([h & t] l')
        (if (f h) i (recur t (inc i))))))

(defn split [f l]
  (let [i (first-index-of f l)]
    (if i
        (let [[l1 l2] (split-at i l)]
          (cons l1 (split f (drop 1 l2))))
        (list l))))

(defn-poly join
  ([ls] (join nil ls))
  ([d ls]
    (if-let [[h & t] ls]
      (reduce #(concat %1 d %2) h t))))

(defn get [l k z]
  (if (and (not-empty? l) (not-empty? (rest l)))
      (let [[h1 h2 & t] l]
        (if (= h1 k) h2 (recur t k z)))
  z))