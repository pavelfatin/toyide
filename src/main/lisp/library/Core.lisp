(def defmacro
  (macro [name params & exprs]
    `(def ~name (macro ~name [~@params] ~@exprs))))

(defmacro defn [name params & exprs]
  `(def ~name (fn ~name [~@params] ~@exprs)))

(defn true? [x]
  (and (boolean? x) x))

(defn false? [x]
  (and (boolean? x) (not x)))

(defmacro when [test & exprs]
  `(if ~test (do ~@exprs)))

(def else true)

(defmacro cond [& clauses]
  (fold-right (fn [[test expr] acc] `(if ~test ~expr ~acc))
              'nil
              (partition 2 clauses)))

(defmacro if-let [[binding-form test] & [then & else]]
  `(let [condition# ~test]
     (if condition#
         (let [~binding-form condition#] ~then)
         ~@else)))

(defmacro defn-poly [name & fs]
  (let [f (fn [[params & exprs]] `((= n ~(count params)) (apply (fn [~@params] ~@exprs) args)))]
    `(def ~name
      (fn [& args]
        (let [n (count args)]
          (cond ~@(mapcat f fs)
                else (error "incorrect argument count")))))))

(defmacro -> [x & exprs]
  (loop [x x, exprs exprs]
    (if-let [[expr & exprs'] exprs]
      (let [x' (if (list? expr)
                   `(~(first expr) ~x ~@(rest expr))
                   (list expr x))]
        (recur x' exprs'))
      x)))

(defmacro ->> [x & exprs]
  (loop [x x, exprs exprs]
    (if-let [[expr & exprs'] exprs]
      (let [x' (if (list? expr)
                   `(~(first expr) ~@(rest expr) ~x)
                   (list expr x))]
        (recur x' exprs'))
      x)))
