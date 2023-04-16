(defn funcFactory [f]
  (fn [& operands]
    (fn [variables] (apply f (map (fn [op] (op variables)) operands)))))

(defn mySumExp [& args]
  (apply + (mapv #(Math/exp %) args)))

(defn mySoft [& args]
  (/ (Math/exp (first args)) (apply mySumExp args)))

(defn myDiv
  ([x] (/ 1.0 x))
  ([dividend & deviders] (/ (double dividend) (apply * deviders))))

(def constant constantly)
(defn variable [var] (fn [args] (get args var)))

(def add (funcFactory +))
(def subtract (funcFactory -))
(def multiply (funcFactory *))
(def negate subtract)
(def divide (funcFactory myDiv))
(def sumexp (funcFactory mySumExp))
(def softmax (funcFactory mySoft))

(def funcList {
               '+       add
               '-       subtract
               '*       multiply
               '/       divide
               'negate  negate
               'sumexp  sumexp
               'softmax softmax
               })

(defn parser [cnst vrb opers]
  (fn to-pars [inputData]
    ((fn myParser [expr]
       (cond
         (list? expr) (apply (opers (first expr)) (map myParser (rest expr)))
         (number? expr) (cnst expr)
         (symbol? expr) (vrb (str expr))))
     (read-string inputData))))

(def parseFunction (parser constant variable funcList))
;======================================================================================================================;

(load-file "proto.clj")

(def toString (method :toString))
(def evaluate (method :evaluate))
(def diff (method :diff))
(def _args (field :args))
(def _val (field :val))

(defn fun-definder [tostr evl dif]
  {:toString tostr
   :evaluate evl
   :diff     dif
   })

(declare ZERO)
(def Const-proto
  (fun-definder
    #(format "%.1f" (double (_val %)))
    (fn [this args] (_val this))
    (fn [this dx] ZERO)))

(declare ONE)
(def Var-proto
  (fun-definder
    #(_val %1)
    (fn [this vars] (vars (_val this)))
    (fn [this dx] (if (= dx (_val this)) ONE ZERO))))

(defn Simple-creator [this val]
  (assoc this :val val))

(def Constant (constructor Simple-creator Const-proto))
(def Variable (constructor Simple-creator Var-proto))
(def ZERO (Constant 0))
(def ONE (Constant 1))

(def Oper-proto
  (let [_sign (field :sign) _oper (field :oper) _deter (field :deter)]
    (fun-definder
      (fn [this] (apply str "(" (_sign this) " " (clojure.string/join " " (mapv toString (_args this))) ")"))
      (fn [this vars] (apply (_oper this) (mapv #(evaluate % vars) (_args this))))
      (fn [this dx] ((_deter this) (_args this) (mapv #(diff % dx) (_args this)))))))

(defn Oper-creator [this & args]
  (assoc this :args args))

(defn op-creator [sign oper deter]
  (constructor Oper-creator {:prototype Oper-proto
                             :sign      sign
                             :oper      oper
                             :deter     deter}))

(def Add
  (op-creator
    '+
    +
    (fn [args deters] (apply Add deters))))

(def Subtract
  (op-creator
    '-
    -
    (fn [args deters] (apply Subtract deters))))

(def Negate
  (op-creator
    'negate
    #(- %)
    (fn [args deters] (apply Negate deters))))

(declare Multiply)
(declare Divide)

(defn diff-mul
  [args deters]
  (second (reduce (fn [[f df] [g dg]]
                    [(Multiply f g)
                     (Add (Multiply f dg)
                          (Multiply df g))])
                  (map vector args deters))))

(defn diff-div [[dividend & deviders] [dividendDif & devidersDif]]
  (if (empty? deviders)
    (Divide (Negate dividendDif)
            (Multiply dividend dividend))
    (let [divisor (apply Multiply deviders)]
      (Divide (Subtract (Multiply dividendDif divisor)
                        (Multiply dividend (diff-mul deviders devidersDif)))
              (Multiply divisor divisor)))))

(def Multiply
  (op-creator
    '*
    *
    diff-mul))

(def Divide
  (op-creator
    '/
    myDiv
    diff-div))

(declare Exp)
(declare Sumexp)
(defn diff-exp [arg deters] (Multiply (Exp arg) deters))
(def Exp (op-creator 'exp #(Math/exp %) diff-exp))
(defn diff-sumexp [args deters] (apply Add (mapv diff-exp args deters)))
(defn diff-soft [args deters] (diff-div [(Exp (first args)) (apply Sumexp args)]
                                       [(diff-exp (first args) (first deters)) (diff-sumexp args deters)]))

(def Sumexp
  (op-creator
    'sumexp
    mySumExp
    diff-sumexp))

(def Softmax
  (op-creator
    'softmax
    mySoft
    diff-soft))

(def objList {
              '+       Add
              '-       Subtract
              '*       Multiply
              '/       Divide
              'negate  Negate
              'sumexp  Sumexp
              'softmax Softmax
              })

(def parseObject (parser Constant Variable objList))
