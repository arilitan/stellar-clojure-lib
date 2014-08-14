(ns stellar.create_data
  (:require [stellar.stellar-api :as api])
  (:require [stellar.core :as core]))

(def account-map (atom {}))
(def ledger-start 322829)
(def ledger-end   322829)



(api/api-req {:method "ledger"})
(api/get-ledger-info ledger-start)

(core/get-ledger-txns ledger-start)

;initializes atom a creates keys from the unique destinations in a ledger range
(reset! account-map (core/get-all-acct-dest-in-range ledger-start ledger-end))

;creates a vector of the account keys in order to iterate through them
(def acct-keys (mapv key @account-map))

;takes the first acct id in the map
(def temp-key (first acct-keys))

;displays it
temp-key

;creates a vector of all the transaction data for the temp-key account
(def temp-trans (core/get-all-txn-data-vec temp-key))

;displays that transaction vector
temp-trans

;uploads that transaction vector to the transactions map for the corresponding acct-id (temp-key in this case)
(swap! account-map assoc-in [temp-key :transactions] temp-trans)

;uploads the accounts statistical data map for the given acct-id (temp-key)
(swap! account-map assoc-in [temp-key :statistics]  (core/process-all-flow-data temp-trans temp-key))

;display average inflow amount for a given account (temp-key)
(get-in @account-map [temp-key :statistics :inflows :avg-txn])

;iterate through all accts in the acct vector
(comment
  (loop [current-id (first acct-keys) cnt 0 trans-vec (core/get-all-txn-data-vec (first acct-keys))]
    (swap! account-map assoc-in [current-id :transactions] trans-vec)
    (swap! account-map assoc-in [current-id :statistics]  (core/process-all-flow-data trans-vec current-id))
    (if (= cnt 0);(dec(count tempkeys)))
      @account-map
      (recur (nth acct-keys (inc cnt))
             (inc cnt)
             (core/get-all-txn-data-vec (nth acct-keys (inc cnt)))))))





