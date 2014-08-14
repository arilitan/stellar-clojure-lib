(ns stellar.stellar-api
  (:require [cheshire.core   :as json]
            [org.httpkit.client :as http]))

(defn api-req [data]
  (->> data
       json/generate-string
       (assoc {} :body)
       (http/post "https://live.stellar.org:9002")
       deref
       :body
       json/parse-string
       clojure.walk/keywordize-keys))

(defn get-previous-txn-id [account-id]
  (-> (api-req {:method "tx"
                 :params [{:transaction
                           (->> (api-req {:method "account_info"
                                          :params [{:account account-id}]})
                                :result
                                :account_data
                                :PreviousTxnID
                                )}]})
    :result))

(defn get-account-info [account-id]
  (-> (api-req {:method "account_info"
                 :params [{:account account-id}]})))

(defn get-account-tx [account-id]
  (-> (api-req {:method "account_tx"
                 :params [{:account account-id
                           :forward "true"}]})))

(defn get-account-tx-ledger-minmax [account-id ledger_min ledger_max offset]
  (-> (api-req {:method "account_tx"
                 :params [{:account account-id
                           :ledger_min ledger_min
                           :ledger_max ledger_max
                           :offset offset}]})))

(defn get-ledger-info [ledger-num]
  (-> (api-req {:method "ledger"
                 :params [{:transactions "true"
                           :expand "true"
                           :ledger_index ledger-num
                           }]})))
(defn get-tx [tx]
  (-> (api-req {:method "tx"
                 :params [{:transaction tx
                           ;:ledger_index ledger-num
                           }]})))
(defn transaction-entry [tx]
  (-> (api-req {:method "transaction_entry"
                 :params [{:tx_hash tx
                           ;:ledger_index ledger-num
                           }]})))

(defn get-tx-history [tx]
  (-> (api-req {:method "tx_history"
                 :params [{:start tx
                           ;:ledger_index ledger-num
                           }]})))

(-> (get-account-tx "gHuAWt6ZQ5jskU7TgyQ8B1c7wH1LRfzyb6")
    :result
    :transactions
    next second
    :tx
    :Destination
    )
