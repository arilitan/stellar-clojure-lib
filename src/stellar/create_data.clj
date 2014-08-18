(ns stellar.create_data
  (:use [stellar.core]
        [clojure.pprint])
  (:require [stellar.stellar-api :as api])
  ;(:require [stellar.core :as core])
  )

;gets the most recent ledger-index
(def current-ledger-index
  (read-string(get-in (api/api-req {:method "ledger"}) [:result :closed :ledger :ledger_index])))

;given a map of acct ids as keys, assocs the transaction vector into the acct-id map
(defn assoc-transactions [themap thekey]
  (assoc-in themap [thekey :transactions] (get-all-txn-data-vec thekey)))

;given a map of acct ids as keys, assocs the statistics map into the values for the acct-id key
(defn assoc-statistics [themap thekey]
  (assoc-in themap [thekey :statistics] (process-all-flow-data (get-in themap [thekey :transactions]) thekey)))

;returns a map with all accounts in the ledger range as keys, and the transaction and statistics data populated as values in the map
(defn get-data-in-ledgers [ledger-start ledger-end]
  (let [acct-dest-map (get-all-acct-dest-in-range ledger-start ledger-end)]
    (let [acct-keys (mapv key acct-dest-map)]
      (reduce assoc-statistics (reduce assoc-transactions acct-dest-map acct-keys) acct-keys)
   )))

;gets the flow statistics for a given account number as the only input
(defn get-flow-data-map [acct-id]
  (process-all-flow-data (get-all-txn-data-vec acct-id) acct-id))

;displays the statistics map in an easy to read format
(defn display-data-map-stats [statmap]
  (println "Current Balance:    " (statmap :current-balance))
  (println "Average Balance:    " (statmap :avg-balance))
  (println "Max Balance:        " (statmap :max-bal))
  (println "Min Balance:        " (statmap :min-bal))
  (println "Max Balance time:   " (statmap :max-time))
  (println "Min Balance time:   " (statmap :min-time))
  (println "Time Difference:    " (statmap :time-dif-max-min))
  (println "Total STR Inflow:   " (get-in statmap [:inflows :total-coins]))
  (println "Total Inlfow Txns:  " (get-in statmap [:inflows :total-txns]))
  (println "Average Inflow Amt: " (get-in statmap [:inflows :avg-txn]))
  (println "Total STR Outflow:  " (get-in statmap [:outflows :total-coins]))
  (println "Total Outflow Txns: " (get-in statmap [:outflows :total-txns]))
  (println "Average Outflow Amt:" (get-in statmap [:outflows :avg-txn])))

;prints a two column table given a vector of vectors and column names
(defn print-table-from-vec [thevec col-one col-two]
  (print-table (mapv #(conj {} {col-two (second %) col-one (first %)}) thevec)))

;prints the outflow frequency map into a nice table
(defn display-outflow-freq [statmap]
  (let [display-vec (into [] (get-in statmap [:outflows :txn-frqcy]))]
    (print-table-from-vec display-vec "Amount" "Frequency")))

;prints the inflow frequency map into a nice table
(defn display-inflow-freq [statmap]
  (let [display-vec (into [] (get-in statmap [:inflows :txn-frqcy]))]
    (print-table-from-vec display-vec "Amount" "Frequency")))

;prints the balance over time data series
(defn display-balance-over-time [statmap]
  (print-table-from-vec (statmap :balance-over-time) "Date" "Total Balance"))







