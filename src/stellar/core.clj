(ns stellar.core
  (:require [stellar.stellar-api :as api])
  (:gen-class))

; curl -X POST https://live.stellar.org:9002 -d '{ "method" : "ledger" }'

;define demographic-data
(def demographic-data
  {:age ""
   :location ""
   :sex ""
   :ethnicity ""
   :education ""})

;define statistics data
(def statistic-data
  {:inflows {:total-coins 0, :total-txns 0, :avg-txn 0, :txn-frqcy {}}
   :outflows {:total-coins 0, :total-txns 0, :avg-txn 0 :txn-frqcy {}}
   :max-bal 0
   :max-time 0
   :min-bal 0
   :min-time 0
   :avg-balance 0
   :current-balance 0
   :time-dif-max-min 0
   :balance-over-time []})

(def other-data
  {:email-address ""
   :facebook-id ""
   :trust-lines [{:trust-acct ""
                  :trust-amt ""
                  :trust-ccy ""}]})

(def acct-data-headers
  {:transactions [],
   :demographics demographic-data
   :statistics statistic-data
   :other-data other-data})


(defn get-txn-count [acct-id ledger-min ledger-max offset]
  (count
   (get-in (api/get-account-tx-ledger-minmax acct-id)
                [:result :transactions])))

;Returns an array of maps for all the transactions in a given ledger
(defn get-ledger-txns [ledger-index]
  (get-in (api/get-ledger-info ledger-index) [:result :ledger :transactions]))

;Returns a vector of all of the account destinations given a ledger index
(defn get-ledger-destinations [ledger-index]
  (mapv :Destination (get-ledger-txns ledger-index)))

;Adds an array of keys on to a map with empty values
(defn add-keys-to-map [some-map vec-of-keys]
  (reduce #(assoc %1 %2 acct-data-headers)some-map vec-of-keys))

;Returns a map with all unique account destinations for all ledgers in a given range as keys and empty maps as values, getting rid of nil values
(defn get-all-acct-dest-in-range [start-range end-range]
  (dissoc
   (reduce add-keys-to-map {} (keep #(get-ledger-destinations %)
                                    (vec (range start-range (inc end-range) ))))
   nil))

;Returns the transactions vector for a given acct-id
(defn get-txn-vec [acct-id]
  (get-in (api/get-account-tx acct-id) [:result :transactions]))

(defn only-payments-vec [txn-vec]
  )

;Returns an vector of all 'tx' transaction data in a given account, given an acct number
(defn get-all-txn-data-vec [acct-id]
  (let [trans-range
    (vec(range 0 (count(get-txn-vec acct-id))))]
  (mapv #((nth (get-txn-vec acct-id) %) :tx) trans-range)))

;updates the txn amount indexing map
(defn update-index-map [frequency-map txn-amount]
  (if (nil? (get frequency-map txn-amount)) ;does this txn-value exist in the map?
    (assoc frequency-map txn-amount 1) ;if not add it
    (update-in frequency-map [txn-amount] inc))) ;if it does increase its frequency


;takes as input either the inflow map or outflow map and updates it according to txn-amount
(defn update-stat-map [stat-map txn-amount]
  (let [temp-map (-> (update-in stat-map [:total-coins] + txn-amount) ;updates the stat-map by txn-amount
                     (update-in [:total-txns] inc))];increases the total-txns by one
    (->
     (assoc-in temp-map [:txn-frqcy] ;adds the txn-amount to the frequency map
               (update-index-map (get temp-map :txn-frqcy) txn-amount))
     (assoc-in [:avg-txn]
               (format "%.0f" (double
               (/ (temp-map :total-coins) (temp-map :total-txns)))))
     )))

;given a transaction map, acct-id, and running stats, returns updated stat map depending on if the transaction is an inflow or outflow
(defn update-flow-stats[txn-map acct-id cur-stat-map]
  (let [acct (txn-map :Account)]
    (cond
      (= acct acct-id) (assoc-in cur-stat-map [:outflows] (update-stat-map (get cur-stat-map :outflows) (read-string (txn-map :Amount))))
      :else (assoc-in cur-stat-map [:inflows] (update-stat-map (get cur-stat-map :inflows) (read-string (txn-map :Amount)))))))

;filter txn-vec by payment-type
(defn filter-txn-by-payment [txn-vec payment-type]
  (filterv #(= (get % :TransactionType) payment-type) txn-vec))

;proccess all of the txn data into a flow map
(defn process-all-flow-data [txn-vec acct-id]
  (let [filtered-vec (filter-txn-by-payment txn-vec "Payment")]
    (loop[flow-stats statistic-data cnt 0 cur-bal 0 cur-bal-sum 0 max-time ((nth filtered-vec 0) :date) min-time ((nth filtered-vec 0) :date) max-bal 0 min-bal 0 balance-over-time []]
      (if (= cnt (dec(count filtered-vec)))
        (let [current-balance (if (= ((nth filtered-vec cnt) :Account) acct-id) (- cur-bal (read-string((nth filtered-vec cnt) :Amount))) (+ cur-bal (read-string((nth filtered-vec cnt) :Amount))))]
        (->
         (update-flow-stats (nth filtered-vec cnt) acct-id flow-stats)
         (assoc
           :max-bal (if (> current-balance max-bal) current-balance max-bal)
           :max-time (if (> current-balance max-bal) ((nth filtered-vec cnt) :date) max-time)
           :min-bal (if (< current-balance min-bal) current-balance min-bal)
           :min-time (if (< current-balance min-bal) ((nth filtered-vec cnt) :date) min-time)
           :current-balance current-balance
           :avg-balance (format "%.0f"(double (/
                                               (+ cur-bal-sum current-balance)
                                               (inc (+ (get-in flow-stats [:inflows :total-txns])(get-in flow-stats [:outflows :total-txns]))))))
           :balance-over-time (conj balance-over-time [((nth filtered-vec cnt) :date)
                                                       (if (= ((nth filtered-vec cnt) :Account) acct-id)
                                                         (- cur-bal (read-string((nth filtered-vec cnt) :Amount)))
                                                         (+ cur-bal (read-string((nth filtered-vec cnt) :Amount))))])
           :time-dif-max-min (- (if (> current-balance max-bal) ((nth filtered-vec cnt) :date) max-time)
                                (if (< current-balance min-bal) ((nth filtered-vec cnt) :date) min-time))
         )))
        (let[cur-map (nth filtered-vec cnt)]
          (recur (update-flow-stats cur-map acct-id flow-stats)
                 (inc cnt)
                 (if (= (cur-map :Account) acct-id) (- cur-bal (read-string(cur-map :Amount))) (+ cur-bal (read-string(cur-map :Amount)))) ;if sending STR current balance is subtracted by txn-amount, otherwise its added
                 (+ cur-bal-sum (if (= (cur-map :Account) acct-id) (- cur-bal (read-string(cur-map :Amount))) (+ cur-bal (read-string(cur-map :Amount)))))
                 (if (> cur-bal max-bal) (cur-map :date) max-time)
                 (if (< cur-bal min-bal) (cur-map :date) min-time)
                 (if (> cur-bal max-bal) cur-bal max-bal)
                 (if (< cur-bal min-bal) cur-bal min-bal)
                 (conj balance-over-time [(cur-map :date)
                                          (if (= (cur-map :Account) acct-id)
                                            (- cur-bal (read-string(cur-map :Amount)))
                                            (+ cur-bal (read-string(cur-map :Amount))))])
            ))))))







