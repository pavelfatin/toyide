(defn flip [f]
  (fn [arg1 arg2] (f arg2 arg1)))

(defn identity [x] x)

(defn const [x] (fn [_] x))

(defn comp [& fs]
  (fn [z] (fold-right #(%1 %2) z fs)))

(defn partial [f & args1]
  (fn [& args2] (apply f (concat args1 args2))))

(defn complement [f] (comp not f))
