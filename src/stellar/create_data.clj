(ns stellar.create_data
  (:require [stellar.stellar-api :as api])
  (:require [stellar.core :as core]))

(def account-map (atom {}))
(def ledger-start 322829)
(def ledger-end   322829)



(api/api-req {:method "ledger"})
(api/get-ledger-info ledger-start)

(core/get-ledger-txns ledger-start)
;(reset! account-map (core/get-all-acct-dest-in-range ledger-start ledger-end))
;(def acct-keys (mapv key @account-map))
;(def temp-key (first acct-keys))
;temp-key
;(def temp-trans (core/get-all-txn-data-vec temp-key))
;temp-trans
;(count tempkeys)
;(swap! account-map assoc-in [temp-key :transactions] temp-trans)
;(swap! account-map assoc-in [temp-key :statistics]  (core/process-all-flow-data temp-trans temp-key))
(comment
  (loop [current-id (first acct-keys) cnt 0 trans-vec (core/get-all-txn-data-vec (first acct-keys))]
    (swap! account-map assoc-in [current-id :transactions] trans-vec)
    (swap! account-map assoc-in [current-id :statistics]  (core/process-all-flow-data trans-vec current-id))
    (if (= cnt 0);(dec(count tempkeys)))
      @account-map
      (recur (nth acct-keys (inc cnt))
             (inc cnt)
             (core/get-all-txn-data-vec (nth acct-keys (inc cnt)))))))





